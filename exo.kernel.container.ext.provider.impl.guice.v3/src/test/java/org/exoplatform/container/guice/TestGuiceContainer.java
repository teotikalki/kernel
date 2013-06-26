/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.container.guice;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.exoplatform.container.RootContainer;
import org.exoplatform.container.jmx.AbstractTestContainer;
import org.exoplatform.container.spi.ComponentAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:nfilotto@exoplatform.com">Nicolas Filotto</a>
 * @version $Id$
 *
 */
public class TestGuiceContainer extends AbstractTestContainer
{

   public void testIntegration()
   {
      RootContainer container = createRootContainer("test-exo-container.xml");
      assertNotNull(container);
      ComponentAdapter adapterA = container.getComponentAdapterOfType(A.class);
      assertNotNull(adapterA);
      assertSame(adapterA, container.getComponentAdapterOfType(A.class));
      ComponentAdapter adapterB = container.getComponentAdapterOfType(B.class);
      assertNotNull(adapterB);
      ComponentAdapter adapterC = container.getComponentAdapterOfType(C.class);
      assertNotNull(adapterC);
      ComponentAdapter adapterD = container.getComponentAdapterOfType(D.class);
      assertNotNull(adapterD);
      assertSame(adapterD, container.getComponentAdapterOfType(D.class));
      ComponentAdapter adapterE = container.getComponentAdapterOfType(E.class);
      assertNotNull(adapterE);
      adapterE = container.getComponentAdapter("MyClassE");
      assertNotNull(adapterE);
      assertSame(adapterE, container.getComponentAdapter("MyClassE"));
      ComponentAdapter adapterF = container.getComponentAdapterOfType(F.class);
      assertNotNull(adapterF);
      ComponentAdapter adapterG = container.getComponentAdapterOfType(G.class);
      assertNotNull(adapterG);
      A a = container.getComponentInstanceOfType(A.class);
      assertNotNull(a);
      assertSame(a, container.getComponentInstanceOfType(A.class));
      assertSame(a, adapterA.getComponentInstance());
      B b = container.getComponentInstanceOfType(B.class);
      assertNotNull(b);
      assertSame(b, container.getComponentInstanceOfType(B.class));
      assertSame(b, adapterB.getComponentInstance());
      C c = container.getComponentInstanceOfType(C.class);
      assertNotNull(c);
      assertNotSame(c, container.getComponentInstanceOfType(C.class));
      assertNotSame(c, adapterC.getComponentInstance());
      assertSame(a, c.a);
      assertSame(b, c.b);
      assertSame(a, ((C)adapterC.getComponentInstance()).a);
      assertSame(b, ((C)adapterC.getComponentInstance()).b);
      assertSame(a, container.getComponentInstanceOfType(C.class).a);
      assertSame(b, container.getComponentInstanceOfType(C.class).b);
      D d = container.getComponentInstanceOfType(D.class);
      assertNotNull(d);
      assertSame(d, container.getComponentInstanceOfType(D.class));
      assertSame(d, adapterD.getComponentInstance());
      assertSame(a, d.a);
      assertSame(b, d.b);
      E e = (E)container.getComponentInstance("MyClassE");
      assertNotNull(a);
      assertSame(e, container.getComponentInstance("MyClassE"));
      assertSame(e, adapterE.getComponentInstance());
      F f = container.getComponentInstanceOfType(F.class);
      assertNotNull(f);
      assertSame(f, container.getComponentInstanceOfType(F.class));
      assertSame(f, adapterF.getComponentInstance());
      assertSame(e, f.e);
      G g = container.getComponentInstanceOfType(G.class);
      assertNotNull(g);
   }

   public static class A
   {
   }

   @Singleton
   public static class B
   {
   }

   public static class C
   {
      @Inject
      A a;

      @Inject
      B b;
   }

   public static class D
   {
      A a;

      B b;

      public D(A a, B b)
      {
         this.a = a;
         this.b = b;
      }
   }

   public static class E
   {
   }

   @Singleton
   public static class F
   {
      @Inject
      @Named("MyClassE")
      E e;
   }

   @Singleton
   @MyClassG
   public static class G
   {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @BindingAnnotation
   @interface MyClassG {
   }
}
