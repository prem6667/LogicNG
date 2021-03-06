///////////////////////////////////////////////////////////////////////////
//                   __                _      _   ________               //
//                  / /   ____  ____ _(_)____/ | / / ____/               //
//                 / /   / __ \/ __ `/ / ___/  |/ / / __                 //
//                / /___/ /_/ / /_/ / / /__/ /|  / /_/ /                 //
//               /_____/\____/\__, /_/\___/_/ |_/\____/                  //
//                           /____/                                      //
//                                                                       //
//               The Next Generation Logic Library                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////
//                                                                       //
//  Copyright 2015-2018 Christoph Zengler                                //
//                                                                       //
//  Licensed under the Apache License, Version 2.0 (the "License");      //
//  you may not use this file except in compliance with the License.     //
//  You may obtain a copy of the License at                              //
//                                                                       //
//  http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                       //
//  Unless required by applicable law or agreed to in writing, software  //
//  distributed under the License is distributed on an "AS IS" BASIS,    //
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      //
//  implied.  See the License for the specific language governing        //
//  permissions and limitations under the License.                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

package org.logicng.handlers;

import org.logicng.datastructures.Assignment;

/**
 * A MaxSAT handler which cancels the solving process after a given timeout.
 * @version 1.0
 * @since 1.0
 */
public final class TimeoutMaxSATHandler implements MaxSATHandler {

  private final long timeout;
  private final SATHandler satHandler;
  private long designatedEnd;
  private int currentLb;
  private int currentUb;

  /**
   * Constructs a new instance with a given timeout in milliseconds.
   * <p>
   * Note that it might take a few milliseconds more until the solver is actually canceled,
   * since the handler depends on the solvers call to {@code foundApproximation()} or {@link SATHandler#detectedConflict()}.
   * @param timeout the timeout in milliseconds
   */
  public TimeoutMaxSATHandler(final long timeout) {
    this.timeout = timeout;
    this.satHandler = new TimeoutSATHandler(timeout);
    this.currentLb = -1;
    this.currentUb = -1;
  }

  @Override
  public SATHandler satHandler() {
    return this.satHandler;
  }

  @Override
  public boolean foundLowerBound(final int lowerBound, final Assignment model) {
    this.currentLb = lowerBound;
    return System.currentTimeMillis() < designatedEnd;
  }

  @Override
  public boolean foundUpperBound(final int upperBound, final Assignment model) {
    this.currentUb = upperBound;
    return System.currentTimeMillis() < designatedEnd;
  }

  @Override
  public void startedSolving() {
    this.satHandler.startedSolving();
    final long start = System.currentTimeMillis();
    this.designatedEnd = start + this.timeout;
  }

  @Override
  public void finishedSolving() {
    // nothing to do here
  }

  @Override
  public int lowerBoundApproximation() {
    return this.currentLb;
  }

  @Override
  public int upperBoundApproximation() {
    return this.currentUb;
  }
}
