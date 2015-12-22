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
//  Copyright 2015 Christoph Zengler                                     //
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

package org.logicng.transformations.cnf;

import org.junit.Assert;
import org.junit.Test;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.F;
import org.logicng.formulas.Formula;
import org.logicng.formulas.Literal;
import org.logicng.io.parser.ParserException;
import org.logicng.io.parser.PropositionalParser;
import org.logicng.predicates.CNFPredicate;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.List;
import java.util.SortedSet;

/**
 * Unit Tests for {@link TseitinTransformation}.
 * @author Christoph Zengler
 * @version 1.0
 * @since 1.0
 */
public class TseitinTest {

  private final TseitinTransformation ts = new TseitinTransformation(0);
  private final CNFPredicate cnfPredicate = new CNFPredicate();

  @Test
  public void testConstants() {
    Assert.assertEquals(F.TRUE, F.TRUE.transform(ts));
    Assert.assertEquals(F.FALSE, F.FALSE.transform(ts));
  }

  @Test
  public void testLiterals() {
    Assert.assertEquals(F.A, F.A.transform(ts));
    Assert.assertEquals(F.NA, F.NA.transform(ts));
  }

  @Test
  public void testBinaryOperators() throws ParserException {
    Assert.assertTrue(F.IMP1.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.IMP1, F.IMP1.transform(ts), F.IMP1.variables()));
    Assert.assertTrue(F.IMP2.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.IMP2, F.IMP2.transform(ts), F.IMP2.variables()));
    Assert.assertTrue(F.IMP3.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.IMP3, F.IMP3.transform(ts), F.IMP3.variables()));
    Assert.assertTrue(F.EQ1.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.EQ1, F.EQ1.transform(ts), F.EQ1.variables()));
    Assert.assertTrue(F.EQ2.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.EQ2, F.EQ2.transform(ts), F.EQ2.variables()));
    Assert.assertTrue(F.EQ3.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.EQ3, F.EQ3.transform(ts), F.EQ3.variables()));
    Assert.assertTrue(F.EQ4.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(F.EQ4, F.EQ4.transform(ts), F.EQ4.variables()));
  }

  @Test
  public void testNAryOperators() throws ParserException {
    PropositionalParser p = new PropositionalParser(F.f);
    Assert.assertEquals(F.AND1, F.AND1.transform(ts));
    Assert.assertEquals(F.OR1, F.OR1.transform(ts));
    final Formula f1 = p.parse("~(a | b) & c & ~(x & ~y) & (w => z)");
    final Formula f2 = p.parse("~(a & b) | c | ~(x | ~y)");
    final Formula f3 = p.parse("a | b | (~x & ~y)");
    Assert.assertTrue(f1.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f1, f1.transform(ts), f1.variables()));
    Assert.assertTrue(f2.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f2, f2.transform(ts), f2.variables()));
    Assert.assertTrue(f3.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f3, f3.transform(ts), f3.variables()));
  }

  @Test
  public void testNot() throws ParserException {
    PropositionalParser p = new PropositionalParser(F.f);
    Assert.assertEquals(p.parse("~a"), p.parse("~a").transform(ts));
    Assert.assertEquals(p.parse("a"), p.parse("~~a").transform(ts));
    Assert.assertEquals(p.parse("a & ~b"), p.parse("~(a => b)").transform(ts));
    final Formula f1 = p.parse("~(~(a | b) => ~(x | y))");
    final Formula f2 = p.parse("~(a <=> b)");
    final Formula f3 = p.parse("~(~(a | b) <=> ~(x | y))");
    final Formula f4 = p.parse("~(a & b & ~x & ~y)");
    final Formula f5 = p.parse("~(a | b | ~x | ~y)");
    Assert.assertTrue(f1.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f1, f1.transform(ts), f1.variables()));
    Assert.assertTrue(f2.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f2, f2.transform(ts), f2.variables()));
    Assert.assertTrue(f3.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f3, f3.transform(ts), f3.variables()));
    Assert.assertTrue(f4.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f4, f4.transform(ts), f4.variables()));
    Assert.assertTrue(f5.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f5, f5.transform(ts), f5.variables()));
    Assert.assertTrue(f5.transform(ts).holds(cnfPredicate));
    Assert.assertTrue(equivalentModels(f5, f5.transform(ts), f5.variables()));
  }

  private boolean equivalentModels(final Formula f1, final Formula f2, final SortedSet<Literal> vars) {
    final SATSolver s = MiniSat.miniSat(f1.factory());
    s.add(f1);
    final List<Assignment> models1 = s.enumerateAllModels(vars);
    s.reset();
    s.add(f2);
    final List<Assignment> models2 = s.enumerateAllModels(vars);
    if (models1.size() != models2.size())
      return false;
    for (final Assignment model : models1)
      if (!models2.contains(model))
        return false;
    return true;
  }
}
