/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.rain.debug;

import java.util.*;

/**
 * @author: Alexander Shubenkov
 * @since: 10.01.14
 */

public class Profiler {
    private long timers[];
    private long rememberedTime;

    public Profiler(int n) {
        timers = new long[n];
    }

    public void remember() {
        rememberedTime = System.nanoTime();
    }

    public void addToTimer(int i) {
        timers[i] += System.nanoTime() - rememberedTime;
    }

    public long getTimer(int i) {
        return timers[i];
    }

    public void reset() {
        for(int i = 0; i < timers.length; i++)
            timers[i] = 0;
    }

    public void print() {
        double all = 0;
        for(int i = 0; i < timers.length; i++)
            all += (double)timers[i];

        for(int i = 0; i < timers.length; i++) {
            System.out.print(timers[i] + "(" + (int)(timers[i] / all * 100f) + "%)-");
        }
        System.out.println();
    }
}
