// -*- mode: c++; c-file-style: "k&r"; c-basic-offset: 4 -*-
/***********************************************************************
 *
 * rdmatransport.h:
 *   message-passing network interface that uses RDMA and libasync
 *
 * Copyright 2013 Dan R. K. Ports  <drkp@cs.washington.edu>
 * Copyright 2018 Irene Zhang <iyzhang@cs.washington.edu>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 **********************************************************************/

#ifndef _LIB_RDMATRANSPORT_H_
#define _LIB_RDMATRANSPORT_H_

#include "lib/configuration.h"
#include "lib/transport.h"
#include "lib/transportcommon.h"

#include <event2/event.h>
#include <event2/buffer.h>
#include <event2/bufferevent.h>

#include <map>
#include <unordered_map>
#include <list>
#include <random>
#include <mutex>
#include <netinet/in.h>
#include <rdma/rdma_cma.h>

class RDMATransportAddress : public TransportAddress
{
public:
    RDMATransportAddress * clone() const;
private:
    RDMATransportAddress(const sockaddr_in &addr);
    
    sockaddr_in addr;
    friend class RDMATransport;
    friend bool operator==(const RDMATransportAddress &a,
                           const RDMATransportAddress &b);
    friend bool operator!=(const RDMATransportAddress &a,
                           const RDMATransportAddress &b);
    friend bool operator<(const RDMATransportAddress &a,
                          const RDMATransportAddress &b);
};

class RDMATransport : public TransportCommon<RDMATransportAddress>
{
public:
    RDMATransport(double dropRate = 0.0, double reogrderRate = 0.0,
                    int dscp = 0, bool handleSignals = true);
    virtual ~RDMATransport();
    void Register(TransportReceiver *receiver,
                  const transport::Configuration &config,
                  int replicaIdx);
    void Run();
    void Stop();
    int Timer(uint64_t ms, timer_callback_t cb);
    bool CancelTimer(int id);
    void CancelAllTimers();
    
private:
    std::mutex mtx;
    struct RDMATransportTimerInfo
    {
        RDMATransport *transport;
        timer_callback_t cb;
        event *ev;
        int id;
    };
    struct RDMATransportRDMAListener
    {
        RDMATransport *transport;
        TransportReceiver *receiver;
        // can find rdma cm channel in id
        struct rdma_cm_id *id;
        // protection domain
        struct ibv_pd *pd;
        // completion queue
        struct ibv_cq *cq;
        // libevent event
        event *cmevent;
        event *cqevent;
        // message passing space
        string sendType;
        string sendData;
        ibv_mr *sendmr[2];
        string recvType;
        string recvData;
        ibv_mr *recvmr[2];
    };
    event_base *libeventBase;
    int lastTimerId;
    std::map<int, RDMATransportTimerInfo *> timers;
    std::list<RDMATransportRDMAListener *> connections;
    std::map<RDMATransportAddress, struct RDMATransportRDMAListener *> rdmaOutgoing;
    std::map<struct RDMATransportRDMAListener *, RDMATransportAddress> rdmaAddresses;
    
    bool SendMessageInternal(TransportReceiver *src,
                             const RDMATransportAddress &dst,
                             const Message &m, bool multicast = false);

    RDMATransportAddress
    LookupAddress(const transport::ReplicaAddress &addr);
    RDMATransportAddress
    LookupAddress(const transport::Configuration &cfg,
                  int replicaIdx);
    RDMATransportAddress*
    BindToPort(struct rdma_cm_id *id, const string &host, const string &port);
    int PostReceive(RDMATransportRDMAListener *info);
    const RDMATransportAddress *
    LookupMulticastAddress(const transport::Configuration*config) { return NULL; };
    void ConnectRDMA(TransportReceiver *src, const RDMATransportAddress &dst);
    void ConnectRDMA(TransportReceiver *src, const RDMATransportAddress &dst,
                     struct rdma_cm_id *id);
    static void CleanupConnection(RDMATransportRDMAListener *info);
    void OnTimer(RDMATransportTimerInfo *info);
    static void TimerCallback(evutil_socket_t fd,
                              short what, void *arg);
    static void LogCallback(int severity, const char *msg);
    static void FatalCallback(int err);
    static void SignalCallback(evutil_socket_t fd,
                               short what, void *arg);
    static void RDMAAcceptCallback(evutil_socket_t fd, short what,
                                  void *arg);
    static void RDMAIncomingCallback(evutil_socket_t fd, short what, void *arg);
    static void RDMAReadableCallback(evutil_socket_t fd, short what,
                                     void *arg);
};

#endif