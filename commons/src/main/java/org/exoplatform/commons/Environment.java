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
package org.exoplatform.commons;

public class Environment
{

   static final public int UNKNOWN = 0;

   static final public int STAND_ALONE = 1;

   static final public int TOMCAT_PLATFORM = 2;

   static final public int JBOSS_PLATFORM = 3;

   static final public int JETTY_PLATFORM = 4;

   static final public int WEBSHPERE_PLATFORM = 5;

   static final public int WEBLOGIC_PLATFORM = 6;

   static private Environment singleton_;

   private int platform_;

   private Environment()
   {
      String catalinaHome = System.getProperty("catalina.home");
      String jbossHome = System.getProperty("jboss.home.dir");
      String jettyHome = System.getProperty("jetty.home");
      String websphereHome = System.getProperty("was.install.root");
      String weblogicHome = System.getProperty("weblogic.Name");
      String standAlone = System.getProperty("maven.exoplatform.dir");
      if (jbossHome != null)
      {
         platform_ = JBOSS_PLATFORM;
      }
      else if (catalinaHome != null)
      {
         platform_ = TOMCAT_PLATFORM;
      }
      else if (jettyHome != null)
      {
         platform_ = JETTY_PLATFORM;
      }
      else if (websphereHome != null)
      {
         platform_ = WEBSHPERE_PLATFORM;
      }
      else if (weblogicHome != null)
      {
         platform_ = WEBLOGIC_PLATFORM;
      }
      else if (standAlone != null)
      {
         platform_ = STAND_ALONE;
      }
      else
      {
         platform_ = UNKNOWN;
      }
   }

   public int getPlatform()
   {
      return platform_;
   }

   static public Environment getInstance()
   {
      if (singleton_ == null)
      {
         synchronized (Environment.class)
         {
            singleton_ = new Environment();
         }
      }
      return singleton_;
   }
}