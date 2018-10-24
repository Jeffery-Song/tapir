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

import com.yahoo.ycsb.Utils;

/**
 * A generator of a zipfian distribution. It produces a sequence of items, such that some items are more popular than others, according
 * to a zipfian distribution. When you construct an instance of this class, you specify the number of items in the set to draw from, either
 * by specifying an itemcount (so that the sequence is of items from 0 to itemcount-1) or by specifying a min and a max (so that the sequence is of 
 * items from min to max inclusive). After you construct the instance, you can change the number of items by calling nextInt(itemcount) or nextLong(itemcount).
 * 
 * Unlike @ZipfianGenerator, this class scatters the "popular" items across the itemspace. Use this, instead of @ZipfianGenerator, if you
 * don't want the head of the distribution (the popular items) clustered together.
 */
public class RangeZipfianGenerator extends RangeGenerator 
{
    public static final double ZETAN=26.46902820178302;
    public static final double USED_ZIPFIAN_CONSTANT=0.99;
    public static final long ITEM_COUNT=10000000000L;

    // final String[]      ranges_;
    // final Integer       rangesize_;
    
    ZipfianGenerator gen;
    long _itemcount;
    
    /******************************* Constructors **************************************/

    public RangeZipfianGenerator(int rangesize, String[] ranges)
    {
        this(rangesize, ranges, ZipfianGenerator.ZIPFIAN_CONSTANT);
    }

    public RangeZipfianGenerator(int rangesize, String[] ranges, double _zipfianconstant)
    {
        super(rangesize, ranges);
        _itemcount = rangesize * ranges_.length;
        if (_zipfianconstant == USED_ZIPFIAN_CONSTANT) 
        {
            gen=new ZipfianGenerator(0,ITEM_COUNT,_zipfianconstant,ZETAN);
        } else {
            gen=new ZipfianGenerator(0,ITEM_COUNT,_zipfianconstant);
        }
    }
    /**************************************************************************************************/
    @Override
    public String nextString() {
        long ret=gen.nextLong();
        ret=Utils.FNVhash64(ret)%_itemcount;
        String key = formatKey((int)ret);
        setLastString(key);
        // setLastInt((int)ret);
        return key;
    }
}
