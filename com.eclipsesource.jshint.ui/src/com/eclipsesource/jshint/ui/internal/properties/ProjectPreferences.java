/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.Configuration;
import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;


public class ProjectPreferences {

  private static final String KEY_ENABLED = "enabled";
  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String KEY_EXCLUDED = "excluded";
  private static final boolean DEF_ENABLED = false;
  private static final String DEF_GLOBALS = "";
  private static final String DEF_OPTIONS = "";
  private static final String DEF_EXCLUDED = "";

  private final Preferences node;
  private boolean changed;

  public ProjectPreferences( IProject project ) {
    node = PreferencesFactory.getProjectPreferences( project );
    changed = false;
  }

  public void setEnabled( boolean enabled ) {
    if( enabled != node.getBoolean( KEY_ENABLED, DEF_ENABLED ) ) {
      if( enabled == DEF_ENABLED ) {
        node.remove( KEY_ENABLED );
      } else {
        node.putBoolean( KEY_ENABLED, enabled );
      }
      changed = true;
    }
  }

  public boolean getEnabled() {
    return node.getBoolean( KEY_ENABLED, DEF_ENABLED );
  }

  public void setExcluded( IResource resource, boolean exclude ) {
    String resourcePath = getResourcePath( resource );
    List<String> excluded = getExcluded();
    if( exclude && !excluded.contains( resourcePath ) ) {
      excluded.add( resourcePath );
    } else if( !exclude && excluded.contains( resourcePath ) ) {
      excluded.remove( resourcePath );
    }
    setExcluded( excluded );
  }

  public boolean getExcluded( IResource resource ) {
    String resourcePath = getResourcePath( resource );
    // projects have resource path == "", they can be disabled but not excluded
    if( "".equals( resourcePath ) ) {
      return false;
    }
    List<String> excludedFiles = getExcluded();
    return excludedFiles.contains( resourcePath );
  }

  private List<String> getExcluded() {
    String value = node.get( KEY_EXCLUDED, DEF_EXCLUDED );
    return decodePath( value );
  }

  private void setExcluded( List<String> excluded ) {
    String value = encodePath( excluded );
    if( !value.equals( node.get( KEY_EXCLUDED, DEF_EXCLUDED ) ) ) {
      if( DEF_EXCLUDED.equals( value ) ) {
        node.remove( KEY_EXCLUDED );
      } else {
        node.put( KEY_EXCLUDED, value );
      }
      changed = true;
    }
  }

  public String getGlobals() {
    return node.get( KEY_GLOBALS, DEF_GLOBALS );
  }

  public void setGlobals( String value ) {
    if( !value.equals( node.get( KEY_GLOBALS, DEF_GLOBALS ) ) ) {
      if( DEF_GLOBALS.equals( value ) ) {
        node.remove( KEY_GLOBALS );
      } else {
        node.put( KEY_GLOBALS, value );
      }
      changed = true;
    }
  }

  public String getOptions() {
    return node.get( KEY_OPTIONS, DEF_OPTIONS );
  }

  public void setOptions( String value ) {
    if( !value.equals( node.get( KEY_OPTIONS, DEF_OPTIONS ) ) ) {
      if( value.equals( DEF_OPTIONS ) ) {
        node.remove( KEY_OPTIONS );
      } else {
        node.put( KEY_OPTIONS, value );
      }
      changed = true;
    }
  }

  public boolean hasChanged() {
    return changed;
  }

  public void save() throws CoreException {
    try {
      node.flush();
      changed = false;
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  public Configuration getConfiguration() {
    String options = getOptions();
    String globals = getGlobals();
    return OptionParserUtil.createConfiguration( options, globals );
  }

  private static String getResourcePath( IResource resource ) {
    return resource.getProjectRelativePath().toPortableString();
  }

  private static String encodePath( List<String> excluded ) {
    StringBuilder builder = new StringBuilder();
    for( String path : excluded ) {
      if( builder.length() > 0 ) {
        builder.append( ':' );
      }
      builder.append( path );
    }
    return builder.toString();
  }

  private static ArrayList<String> decodePath( String value ) {
    ArrayList<String> list = new ArrayList<String>();
    for( String path : value.split( ":" ) ) {
      list.add( path );
    }
    return list;
  }

}
