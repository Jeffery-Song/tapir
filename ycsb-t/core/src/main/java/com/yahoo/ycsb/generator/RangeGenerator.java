/**                                                                                                                                                                                
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.                                                                                                                             
 *                                                                                                                                                                                 
 * Licensed under the Apache License, Version 2.0 (the "License"); you                                                                                                             
 * may not use this file except in compliance with the License. You                                                                                                                
 * may obtain a copy of the License at                                                                                                                                             
 *                                                                                                                                                                                 
 * http://www.apache.org/licenses/LICENSE-2.0                                                                                                                                      
 *                                                                                                                                                                                 
 * Unless required by applicable law or agreed to in writing, software                                                                                                             
 * distributed under the License is distributed on an "AS IS" BASIS,                                                                                                               
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or                                                                                                                 
 * implied. See the License for the specific language governing                                                                                                                    
 * permissions and limitations under the License. See accompanying                                                                                                                 
 * LICENSE file.                                                                                                                                                                   
 */

package com.yahoo.ycsb.generator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates a sequence of integers 0, 1, ...
 */
public abstract class RangeGenerator extends Generator
{
    // final AtomicInteger counter;
    final String[]      ranges_;
    final Integer       rangesize_;
    final String        formatstring_;
    String              laststring_;

    /**
     * Create a counter that starts at countstart
     */
    public RangeGenerator(int rangesize, String[] ranges)
    {
        rangesize_ = rangesize;
        ranges_ = ranges;
        // counter = new AtomicInteger(0);
        // setLastInt(-1);
        laststring_ = "-1";
        formatstring_ = "%0" + (("" + (rangesize_-1)).length()) + "d";
        // counter=new AtomicInteger(countstart);
        // setLastInt(counter.get()-1);
    }
    public String formatKey(int num) {
        return ranges_[num/rangesize_] + String.format(formatstring_, num%rangesize_);
    }
    public String formatKey(int rangeindex, int offset) {
        return ranges_[rangeindex] + String.format(formatstring_, offset);
    }
    /**
     * If the generator returns numeric (integer) values, return the next value as an int. Default is to return -1, which
     * is appropriate for generators that do not return numeric values.
     */
    // @Override
    // public String nextString();
    // {
    //     int ret = counter.getAndIncrement();
    //     String key = ranges_[ret/rangesize_] + String.format(formatstring_, ret%rangesize_);// rangestep_ * ret - (rangestep_ - 1) * (ret % rangesize_) + actualstart_;
    //     laststring_ = key;
    //     return key;
    // }
    public void setLastString(String key) {
        laststring_ = key;
    }

    @Override
    public String lastString()
    {
        return laststring_;
    }
    // @Override
    // public int lastInt()
    // {
    //                 return counter.get() - 1;
    // }
}
