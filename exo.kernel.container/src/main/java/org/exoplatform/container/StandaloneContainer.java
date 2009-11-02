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
package org.exoplatform.container;

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.configuration.ConfigurationManagerImpl;
import org.exoplatform.container.jmx.MX4JComponentAdapterFactory;
import org.exoplatform.container.monitor.jvm.J2EEServerInfo;
import org.exoplatform.container.util.ContainerUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gennady.azarenkov@exoplatform.com">Gennady Azarenkov
 *         </a>
 * @version $Id: StandaloneContainer.java 7168 2006-07-19 07:36:23Z peterit $
 *          Singletone, context independent Exo Container with one configuration
 *          entry point. The configuration is set as follows: - client calls
 *          setConfigurationURL() or setConfigurationPath method BEFORE
 *          getInstance() - otherwise container in instantiation time looks for
 *          configuration.xml file in the "home" directory. the home directory
 *          it is AS server home in a case of AS env or just current directory
 *          (from where JVM is started) for standalone. See
 */

public class StandaloneContainer extends ExoContainer implements SessionManagerContainer
{

   private static final long serialVersionUID = 12L;

   private static final String CONFIGURATION_URL_ATTR = "configurationURL";

   private static StandaloneContainer container;

   // TODO use ONLY attribute from context instead
   private static URL configurationURL = null;

   private static boolean useDefault = true;

   private SessionManager smanager_;

   private ConfigurationManagerImpl configurationManager;

   /**
    * Private StandaloneContainer constructor.
    *
    * @param configClassLoader ClassLoader 
    */
   private StandaloneContainer(ClassLoader configClassLoader)
   {
      super(new MX4JComponentAdapterFactory(), null);
      configurationManager = new ConfigurationManagerImpl(configClassLoader);
      this.registerComponentInstance(ConfigurationManager.class, configurationManager);
      registerComponentImplementation(SessionManagerImpl.class);
   }

   /**
    * Shortcut for getInstance(null, null).
    * 
    * @return the StandaloneContainer instance
    * @throws Exception if error occurs
    */
   public static StandaloneContainer getInstance() throws Exception
   {
      return getInstance(null, null);
   }

   /**
    * Shortcut for getInstance(configClassLoader, null).
    * 
    * @param configClassLoader ClassLoader
    * @return the StandaloneContainer instance
    * @throws Exception if error occurs
    */
   public static StandaloneContainer getInstance(ClassLoader configClassLoader) throws Exception
   {
      return getInstance(configClassLoader, null);
   }

   /**
    * Shortcut for getInstance(null, components).
    * 
    * @param components Object[][]
    * @return the StandaloneContainer instance
    * @throws Exception if error occurs
    */
   public static StandaloneContainer getInstance(Object[][] components) throws Exception
   {
      return getInstance(null, components);
   }

   /**
    * A way to inject externally instantiated objects to container before it
    * starts Object[][] components - an array of components in form: {{"name1",
    * component1}, {"name2", component2}, ...}.
    * 
    * @param configClassLoader ClassLoader 
    * @param components Object[][] 
    * @return the StandaloneContainer instance
    * @throws Exception if error occurs
    */
   public static StandaloneContainer getInstance(ClassLoader configClassLoader, Object[][] components) throws Exception
   {
      if (container == null)
      {
         container = new StandaloneContainer(configClassLoader);
         ExoContainerContext.setTopContainer(container);
         if (useDefault)
            container.initDefaultConf();
         // initialize configurationURL
         initConfigurationURL(configClassLoader);
         container.populate(configurationURL);
         if (components != null)
            container.registerArray(components);
         container.start();
         System.setProperty("exo.standalone-container", StandaloneContainer.class.getName());
         System.out.println("StandaloneContainer initialized using:  " + configurationURL);
      }
      return container;
   }

   protected void registerArray(Object[][] components)
   {
      for (Object[] comp : components)
      {
         if (comp.length != 2)
            continue;
         if (comp[0] == null || comp[1] == null)
            continue;
         if (comp[0].getClass().getName() != String.class.getName())
            continue;
         String n = (String)comp[0];
         Object o = comp[1];
         container.registerComponentInstance(n, o);
         System.out.println("StandaloneContainer: injecting \"" + n + "\"");
      }
   }

   /**
    * Add configuration URL. Plugable way of configuration. Add the configuration to existing configurations set.
    *
    * @param url, URL of location to configuration file
    * @throws MalformedURLException if path is wrong
    */
   public static void addConfigurationURL(String url) throws MalformedURLException
   {
      if ((url == null) || (url.length() == 0))
         return;
      URL confURL = new URL(url);
      configurationURL = fileExists(confURL) ? confURL : null;
   }

   /**
    * Set configuration URL. The configuration should contains all required components configured.
    *
    * @param url, URL of location to configuration file
    * @throws MalformedURLException if path is wrong
    */
   public static void setConfigurationURL(String url) throws MalformedURLException
   {
      useDefault = false;
      addConfigurationURL(url);
   }

   /**
    * Add configuration path. Plugable way of configuration. Add the configuration to existing configurations set.
    *
    * @param path, path to configuration file
    * @throws MalformedURLException if path is wrong
    */
   public static void addConfigurationPath(String path) throws MalformedURLException
   {
      if ((path == null) || (path.length() == 0))
         return;
      URL confURL = new File(path).getAbsoluteFile().toURL();
      configurationURL = fileExists(confURL) ? confURL : null;
   }

   /**
    * Set configuration path. The configuration should contains all required components configured.  
    *
    * @param path, path to configuration file  
    * @throws MalformedURLException if path is wrong
    */
   public static void setConfigurationPath(String path) throws MalformedURLException
   {
      useDefault = false;
      addConfigurationPath(path);
   }

   /**
    * Ccreate SessionContainer.
    *
    * @param id String 
    * @return SessionContainer instance
    */
   public SessionContainer createSessionContainer(String id)
   {
      SessionContainer scontainer = getSessionManager().getSessionContainer(id);
      if (scontainer != null)
         getSessionManager().removeSessionContainer(id);
      scontainer = new SessionContainer(id, null);
      getSessionManager().addSessionContainer(scontainer);
      SessionContainer.setInstance(scontainer);
      return scontainer;
   }

   /**
    * {@inheritDoc}
    */
   public SessionContainer createSessionContainer(String id, String owner)
   {
      return createSessionContainer(id);
   }

   /**
    * {@inheritDoc}
    */
   public List<SessionContainer> getLiveSessions()
   {
      return getSessionManager().getLiveSessions();
   }

   /**
    * {@inheritDoc}
    */
   public void removeSessionContainer(String sessionID)
   {
      getSessionManager().removeSessionContainer(sessionID);
   }

   /**
    * Get configurationURL.
    *
    * @return URL
    */
   public URL getConfigurationURL()
   {
      return configurationURL;
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      super.stop();
      ExoContainerContext.setTopContainer(null);
   }

   // -------------- Helpers ----------

   /**
    * {@inheritDoc}
    */
   public SessionManager getSessionManager()
   {
      if (smanager_ == null)
         smanager_ = (SessionManager)this.getComponentInstanceOfType(SessionManager.class);
      return smanager_;
   }

   private static boolean fileExists(URL url)
   {
      try
      {
         url.openStream().close();
         return true;
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Implements strategy of choosing configuration for this container.
    * 
    * @throws MalformedURLException
    * @throws ConfigurationException
    */
   private static void initConfigurationURL(ClassLoader configClassLoader) throws MalformedURLException,
      ConfigurationException
   {
      // (1) set by setConfigurationURL or setConfigurationPath
      // or
      if (configurationURL == null)
      {

         // (2) exo-configuration.xml in AS (standalone) home directory
         configurationURL = new URL("file:" + (new J2EEServerInfo()).getServerHome() + "/exo-configuration.xml");

         // (3) conf/exo-configuration.xml in war/ear(?)
         if (!fileExists(configurationURL) && configClassLoader != null)
         {
            configurationURL = configClassLoader.getResource("conf/exo-configuration.xml");
         }
      }
   }

   private void initDefaultConf() throws Exception
   {
      configurationManager.addConfiguration(ContainerUtil.getConfigurationURL("conf/configuration.xml"));
      configurationManager.addConfiguration(ContainerUtil.getConfigurationURL("conf/portal/configuration.xml"));
      try
      {
         configurationManager.addConfiguration("war:/conf/configuration.xml");
      }
      catch (Exception ex)
      {
         // TODO https://jira.jboss.org/jira/browse/EXOJCR-198
         //System.err.println("Error of default config init: ");
         //ex.printStackTrace();
      }
   }

   private void populate(URL conf) throws Exception
   {
      configurationManager.addConfiguration(conf);
      configurationManager.processRemoveConfiguration();
      ContainerUtil.addComponents(this, configurationManager);
   }

}