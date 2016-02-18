/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.services.idgenerator.impl;

import org.exoplatform.services.idgenerator.IDGeneratorService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Random;

/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Oct 14, 2004
 * @version $Id: IDGeneratorServiceImpl.java 5332 2006-04-29 18:32:44Z geaz $
 */
public class IDGeneratorServiceImpl implements IDGeneratorService
{
   /**
    * The logger
    */
   private static final Log LOG = ExoLogger.getLogger("exo.kernel.component.common.IDGeneratorServiceImpl");

   private static String hexServerIP_ = null;

   private static final ThreadLocal<Random> SEEDER = new ThreadLocal<Random>() {
                                                              @Override
                                                              protected Random initialValue() {
                                                                  return new Random();
                                                              }
                                                          };

   public Serializable generateID(final Object o)
   {
      return generateStringID(o);
   }

   public long generateLongID(final Object o)
   {
      final String uuid = generateStringID(o);
      return uuid.hashCode();
   }

   public int generatIntegerID(final Object o)
   {
      final String uuid = generateStringID(o);
      return uuid.hashCode();
   }

   public String generateStringID(final Object o)
   {
      final StringBuffer tmpBuffer = new StringBuffer(16);
      if (hexServerIP_ == null) {
        InetAddress localInetAddress = null;
        try {
          // get the inet address
          localInetAddress = InetAddress.getLocalHost();
        } catch (final java.net.UnknownHostException uhe) {
          LOG.error(uhe.getLocalizedMessage(), uhe);
          return null;
        }
        final byte serverIP[] = localInetAddress.getAddress();
        hexServerIP_ = hexFormat(getInt(serverIP), 8);
      }
      final String hashcode = hexFormat(System.identityHashCode(o), 8);
      tmpBuffer.append(hexServerIP_);
      tmpBuffer.append(hashcode);

      final long timeNow = System.currentTimeMillis();
      final int timeLow = (int) timeNow & 0xFFFFFFFF;
      final int node = SEEDER.get().nextInt();

      final StringBuffer guid = new StringBuffer(32);
      guid.append(hexFormat(timeLow, 8));
      guid.append(tmpBuffer.toString());
      guid.append(hexFormat(node, 8));
      return guid.toString();
   }

   private static int getInt(final byte bytes[])
   {
      int i = 0;
      int j = 24;
      for (int k = 0; j >= 0; k++) {
        final int l = bytes[k] & 0xff;
        i += l << j;
        j -= 8;
      }
      return i;
   }

   private static String hexFormat(final int i, final int j)
   {
      final String s = Integer.toHexString(i);
      return padHex(s, j) + s;
   }

   private static String padHex(final String s, final int i) {
      final StringBuffer tmpBuffer = new StringBuffer();
      if (s.length() < i) {
        for (int j = 0; j < i - s.length(); j++) {
          tmpBuffer.append('0');
        }
      }
      return tmpBuffer.toString();
    }
}
