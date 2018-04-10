package de.in.tum.i4.hp2sat.modeling;

import de.in.tum.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.in.tum.i4.hp2sat.testutil.ExampleProvider;
import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CausalModelTest {
    FormulaFactory f;

    @Before
    public void setUp() throws Exception {
        f = new FormulaFactory();
    }

    @Test
    public void Should_CreateCausalModel_When_EverythingFine() throws InvalidCausalModelException {
        ExampleProvider.billySuzy();
    }

    @Test
    public void Should_NotThrowException_When_EverythingFine() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable cExo = f.variable("c");

        Equation equationA = new Equation(a, b);
        Equation equationB = new Equation(b, cExo);

        CausalModel causalModel = new CausalModel(null, new HashSet<>(Arrays.asList(equationA, equationB)),
                new HashSet<>(Collections.singletonList(cExo)));
    }

    @Test(expected = InvalidCausalModelException.class)
    public void Should_ThrowException_When_TwoEquationsDefineSameVariable() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable cExo = f.variable("c");

        Equation equationA1 = new Equation(a, b);
        Equation equationA2 = new Equation(a, cExo);

        CausalModel causalModel = new CausalModel(null, new HashSet<>(Arrays.asList(equationA1, equationA2)),
                new HashSet<>(Collections.singletonList(cExo)));
    }

    @Test(expected = InvalidCausalModelException.class)
    public void Should_ThrowException_When_VariableIsDefinedByEquationAndExogenous() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable cExo = f.variable("c");

        Equation equationA = new Equation(a, b);
        Equation equationB = new Equation(b, cExo);

        CausalModel causalModel = new CausalModel(null, new HashSet<>(Arrays.asList(equationA, equationB)),
                new HashSet<>(Arrays.asList(b, cExo)));
    }

    @Test(expected = InvalidCausalModelException.class)
    public void Should_BeInvalid_When_NotEachVariableDefinedByEquation() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable c = f.variable("c");

        Equation equationA = new Equation(a, b);
        Equation equationB = new Equation(b, c);

        CausalModel causalModel = new CausalModel(null, new HashSet<>(Arrays.asList(equationA, equationB)),
                new HashSet<>());
    }

    @Test(expected = InvalidCausalModelException.class)
    public void Should_BeValid_When_CircularDependency1() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable cExo = f.variable("c");
        Variable d = f.variable("d");

        Equation equationA = new Equation(a, b);
        Equation equationB = new Equation(b, d);
        Equation equationD = new Equation(d, a); // circular: a -> b -> d -> a

        CausalModel causalModel = new CausalModel(null,
                new HashSet<>(Arrays.asList(equationA, equationB, equationD)),
                new HashSet<>(Collections.singletonList(cExo)));
    }

    @Test(expected = InvalidCausalModelException.class)
    public void Should_BeValid_When_CircularDependency2() throws InvalidCausalModelException {
        Variable a = f.variable("a");
        Variable b = f.variable("b");
        Variable cExo = f.variable("c");
        Variable d = f.variable("d");

        Equation equationA = new Equation(a, f.and(b, a)); // circular: a -> b,a
        Equation equationB = new Equation(b, d);
        Equation equationD = new Equation(d, cExo);

        CausalModel causalModel = new CausalModel(null,
                new HashSet<>(Arrays.asList(equationA, equationB, equationD)),
                new HashSet<>(Collections.singletonList(cExo)));
    }
}