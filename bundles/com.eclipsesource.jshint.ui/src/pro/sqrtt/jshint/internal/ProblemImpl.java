/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package pro.sqrtt.jshint.internal;

import pro.sqrtt.jshint.Problem;


public class ProblemImpl implements Problem {
  private final int line;
  private final int character;
  private final String message;
  private final String code;

  public ProblemImpl( int line, int character, String message, String code ) {
    this.line = line;
    this.character = character;
    this.message = message;
    this.code = code;
  }

  public int getLine() {
    return line;
  }

  public int getCharacter() {
    return character;
  }

  public String getMessage() {
    return message;
  }

  public String getCode() {
    return code;
  }

  public int isError() {
    if (code != null && code.length() > 0 && code.charAt( 0 ) == 'E') {
    	return 1;
    } else if (code != null && code.length() > 0 && code.charAt( 0 ) == 'I'){
    	return 2;
    };
    return 0;
  }

}
