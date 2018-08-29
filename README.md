# hp2sat

## Background
This library allows to determine actual causality according to the modified Halpern-Pearl definition of causality [1]
. The used examples in the unit test cases (specifically in [CausalitySolverInstanceTest](
./src/test/java/edu/hp2sat/causality/CausalitySolverInstanceTest.java)) are described [here](./doc/evaluated-models.pdf).

## Installation

```bash
$ mvn install
```

## Usage

### General

Creation of a causal model:
```java
FormulaFactory f = new FormulaFactory();
Variable BTExo = f.variable("BT_exo");
Variable STExo = f.variable("ST_exo");

Variable BT = f.variable("BT");
Variable ST = f.variable("ST");
Variable BH = f.variable("BH");
Variable SH = f.variable("SH");
Variable BS = f.variable("BS");

Formula BTFormula = BTExo;
Formula STFormula = STExo;
Formula SHFormula = ST;
Formula BHFormula = f.and(BT, f.not(SH));
Formula BSFormula = f.or(SH, BH);

Equation BTEquation = new Equation(BT, BTFormula);
Equation STEquation = new Equation(ST, STFormula);
Equation SHEquation = new Equation(SH, SHFormula);
Equation BHEquation = new Equation(BH, BHFormula);
Equation BSEquation = new Equation(BS, BSFormula);

Set<Equation> equations = new HashSet<>(Arrays.asList(BTEquation, STEquation, SHEquation,
    BHEquation, BSEquation));
Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(BTExo, STExo));

CausalModel causalModel = new CausalModel("RockThrowing", equations, exogenousVariables, f);
```

Check whether *ST = 1* is a cause of *BS = 1* in the previously created causal model given *ST_exo, BT_exo = 1*:
```java
Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("BT_exo", true),
    f.literal("ST_exo", true)));
Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
Formula phi = f.variable("BS");
CausalitySolverResult causalitySolverResult =
    CauscausalModel.isCause(context, phi, cause, SolvingStrategy.SAT);
```

### Important Notes

- When working with a causal model, *always* use the same `FormulaFactory` instance. If not, an exception might occur.
- When creating a `CausalModel`, it is checked whether the latter is valid. It needs to fulfill the following 
characteristics; otherwise an exception is thrown:
    - Each variable needs to be either exogenous or defined by *exactly one* equation.
    - The causal model must be *acyclic*. That is, no variables are allowed to mutually depend on each other 
    (directly and indirectly)
    - Variables must not be named with `"_dummy"`.
    
## Literature

[1] J. Y. Halpern. "A Modification of the Halpern-Pearl Definition of Causality." In: Proceedings of the Twenty-Fourth International Joint Conference on Artificial Intelligence, IJCAI 2015, Buenos Aires, Argentina, July 25-31, 2015. 2015, pp. 3022–3033.