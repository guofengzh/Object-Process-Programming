/*******************************************************************************
 * Copyright (c) 2015 Arieh "Vainolo" Bibliowicz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.vainolo.phd.opp.interpreter;

import static com.vainolo.phd.opp.utilities.OPPLogger.*;

import org.eclipse.core.runtime.Path;

import com.vainolo.phd.opp.interpreter.builtin.OPPCompareProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.OPPConceptualProcess;
import com.vainolo.phd.opp.interpreter.builtin.OPPCopyObjectProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.OPPGetDateProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.OPPSleepProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.OPPCompareProcessInstance.ComparisonType;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPAddFirstPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPAddLastPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPGetFirstPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPGetLastPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPGetPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPHasPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPHasPartValueProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPHasPartsProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPObjectCreatingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPRemoveFirstPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPRemoveLastPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPAddPartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.OPPRemovePartProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.complex.OPPPartAddingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.complex.OPPPartFetchingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.complex.OPPPartRemovingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.general.OPPElementCountingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPFirstElementAddingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPFirstElementFetchingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPFirstElementRemovingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLastElementAddingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLastElementFetchingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLastElementRemovingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLocationElementAddingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLocationElementFetchingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.composite.list.OPPLocationElementRemovingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPConsoleReadingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPConsoleWritingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPDialogTextReadingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPDialogTextWritingProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPReadTextFileProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.io.OPPTransformJSONStringToObjectProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.math.OPPBinaryMathOpProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.math.OPPBinaryMathOpProcessInstance.OPPBinaryMathOpType;
import com.vainolo.phd.opp.interpreter.builtin.math.OPPUnaryMathOpProcessInstance.OPPUnaryMathOpType;
import com.vainolo.phd.opp.interpreter.builtin.math.OPPUnaryMathOpProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.twitter.OPPInitializeTwitterClientProcessInstance;
import com.vainolo.phd.opp.interpreter.builtin.twitter.OPPSearchTwitter;
import com.vainolo.phd.opp.interpreter.builtin.web.OPPCallWebAPIProcessInstance;
import com.vainolo.phd.opp.interpreter.inzoomedprocessinstance.OPPInZoomedProcessExecutableInstance;
import com.vainolo.phd.opp.model.OPPObjectProcessDiagram;
import com.vainolo.phd.opp.model.OPPProcess;
import com.vainolo.phd.opp.utilities.OPPFileUtils;

public class OPPProcessInstanceFactory {

  public static OPPProcessInstance createExecutableInstance(OPPObjectProcessDiagram opd) {
    switch (opd.getKind()) {
    case COMPOUND:
      return new OPPInZoomedProcessExecutableInstance(opd);
    case UNFOLDED:
      logInfo("Unfolded OPDs can't be executed.");
      throw new IllegalArgumentException("Unfolded OPDs cannot be executed");
    case SYSTEM:
      logInfo("System OPDs can't be executed yet.");
      throw new IllegalArgumentException("System OPDs can't be executed yet.");
    }
    return null;
  }

  public static OPPProcessInstance createExecutableInstance(String opdName) {
    OPPObjectProcessDiagram opd = OPPFileUtils.loadOPPFile(OPPInterpreter.container.getFile(new Path(opdName + ".opp")).getFullPath().toString());
    return createExecutableInstance(opd);

  }

  public static OPPProcessInstance createExecutableInstance(OPPProcess process) {
    OPPProcessInstance executableInstance = null;
    switch (process.getKind()) {
    case BUILT_IN:
    case COMPOUND:
      logFinest("Searching for built-in process named {0}.", process.getName());
      executableInstance = createBuiltInProcess(process.getName());
      if (executableInstance == null) {
        logFinest("Built-in process {0} not found, searching compound processes.", process.getName());
        executableInstance = createExecutableInstance(process.getName());
      }
      break;
    case CONCEPTUAL:
      executableInstance = new OPPConceptualProcess(process);
      break;
    case JAVA:
      executableInstance = new OPPJavaProcessExecutableInstance(process);
      break;
    }

    return executableInstance;
  }

  private static OPPProcessInstance createBuiltInProcess(String name) {

    switch (name.toLowerCase()) {
    // runtime
    case "object creating":
    case "create object":
      return new OPPObjectCreatingProcessInstance();

    // math
    case "+":
    case "adding":
      return new OPPBinaryMathOpProcessInstance(OPPBinaryMathOpType.ADD);
    case "-":
    case "subtracting":
      return new OPPBinaryMathOpProcessInstance(OPPBinaryMathOpType.SUBS);
    case "*":
    case "multiplying":
      return new OPPBinaryMathOpProcessInstance(OPPBinaryMathOpType.MULT);
    case "/":
    case "dividing":
      return new OPPBinaryMathOpProcessInstance(OPPBinaryMathOpType.DIV);
    case "^":
    case "power":
      return new OPPBinaryMathOpProcessInstance(OPPBinaryMathOpType.POW);

    // Collections
    case "element counting":
    case "count":
      return new OPPElementCountingProcessInstance();

    // List
    case "first element adding":
    case "add first":
      return new OPPFirstElementAddingProcessInstance();
    case "first element fetching":
    case "get first":
      return new OPPFirstElementFetchingProcessInstance();
    case "first element removing":
    case "remove first":
      return new OPPFirstElementRemovingProcessInstance();
    case "location element adding":
    case "add element":
      return new OPPLocationElementAddingProcessInstance();
    case "location element fetching":
    case "fetch element":
      return new OPPLocationElementFetchingProcessInstance();
    case "location element removing":
    case "remove element":
      return new OPPLocationElementRemovingProcessInstance();
    case "last element adding":
    case "add last":
      return new OPPLastElementAddingProcessInstance();
    case "last element fetching":
    case "fetch last":
      return new OPPLastElementFetchingProcessInstance();
    case "last element removing":
    case "remove last":
      return new OPPLastElementRemovingProcessInstance();

    // Complex Object
    case "part adding":
    case "add part":
      return new OPPPartAddingProcessInstance();
    case "part fetching":
    case "fetch part":
      return new OPPPartFetchingProcessInstance();
    case "part removing":
    case "remove part":
      return new OPPPartRemovingProcessInstance();

    // IO
    case "console reading":
    case "console input":
      return new OPPConsoleReadingProcessInstance();
    case "console writing":
    case "console output":
      return new OPPConsoleWritingProcessInstance();
    case "dialog text reading":
    case "dialog input":
      return new OPPDialogTextReadingProcessInstance();
    case "dialog text writing":
    case "dialog output":
      return new OPPDialogTextWritingProcessInstance();

    }

    // Composite
    if (name.equalsIgnoreCase("New Instance")) {

    } else if (name.equalsIgnoreCase("Add First Part")) {
      return new OPPAddFirstPartProcessInstance();
    } else if (name.equalsIgnoreCase("Add Last Part")) {
      return new OPPAddLastPartProcessInstance();
    } else if (name.equalsIgnoreCase("Add Part")) {
      return new OPPAddPartProcessInstance();
    } else if (name.equalsIgnoreCase("Get First Part")) {
      return new OPPGetFirstPartProcessInstance();
    } else if (name.equalsIgnoreCase("Get Last Part")) {
      return new OPPGetLastPartProcessInstance();
    } else if (name.equalsIgnoreCase("Get Part")) {
      return new OPPGetPartProcessInstance();
    } else if (name.equalsIgnoreCase("Remove First Part")) {
      return new OPPRemoveFirstPartProcessInstance();
    } else if (name.equalsIgnoreCase("Remove Last Part")) {
      return new OPPRemoveLastPartProcessInstance();
    } else if (name.equalsIgnoreCase("Remove Part")) {
      return new OPPRemovePartProcessInstance();
    } else if (name.equalsIgnoreCase("Has Part")) {
      return new OPPHasPartProcessInstance();
    } else if (name.equalsIgnoreCase("Has Part Value")) {
      return new OPPHasPartValueProcessInstance();
    } else if (name.equalsIgnoreCase("Has Parts")) {
      return new OPPHasPartsProcessInstance();

      // Input and Output
    } else if (name.equalsIgnoreCase("Console Input")) {
      return new OPPConsoleReadingProcessInstance();
    } else if (name.equalsIgnoreCase("Console Output")) {
      return new OPPConsoleWritingProcessInstance();
    } else if (name.equalsIgnoreCase("Dialog Input")) {
      return new OPPDialogTextReadingProcessInstance();
    } else if (name.equalsIgnoreCase("Dialog Output")) {
      return new OPPDialogTextWritingProcessInstance();

      // Math and Data
      // } else if (name.equals("a+b") || name.equals("+")) {
      // } else if (name.equals("a-b") || name.equals("-")) {
      // } else if (name.equals("a*b") || name.equals("*")) {
      // } else if (name.equals("a/b") || name.equals("/")) {
      // } else if (name.equals("a^b") || name.equals("^")) {
    } else if (name.equals("a<=b") || name.equals("<=")) {
      return new OPPCompareProcessInstance(ComparisonType.LESS_THAN_OR_EQUAL);
    } else if (name.equals("a>=b") || name.equals(">=")) {
      return new OPPCompareProcessInstance(ComparisonType.GREATER_THAN_OR_EQUAL);
    } else if (name.equals("a>b") || name.equals(">")) {
      return new OPPCompareProcessInstance(ComparisonType.GREATER_THAN);
    } else if (name.equals("a<b") || name.equals("<")) {
      return new OPPCompareProcessInstance(ComparisonType.LESS_THAN);
    } else if (name.equals("a==b") || name.equals("==")) {
      return new OPPCompareProcessInstance(ComparisonType.EQUAL);
    } else if (name.equals("log(a)") || name.equals("log")) {
      return new OPPUnaryMathOpProcessInstance(OPPUnaryMathOpType.LOG);
    } else if (name.equals("-a") || name.equals("-")) {
      return new OPPUnaryMathOpProcessInstance(OPPUnaryMathOpType.NEG);
    } else if (name.equals("sqrt(a)") || name.equals("sqrt")) {
      return new OPPUnaryMathOpProcessInstance(OPPUnaryMathOpType.SQRT);

      // Misc
    } else if (name.equalsIgnoreCase("Sleep")) {
      return new OPPSleepProcessInstance();
    } else if (name.equalsIgnoreCase("Call Web API")) {
      return new OPPCallWebAPIProcessInstance();
    } else if (name.equalsIgnoreCase("Initialize Twitter Client")) {
      return new OPPInitializeTwitterClientProcessInstance();
    } else if (name.equalsIgnoreCase("Search Twitter")) {
      return new OPPSearchTwitter();
    } else if (name.equalsIgnoreCase("Get Date")) {
      return new OPPGetDateProcessInstance();
    } else if (name.equalsIgnoreCase("Copy Object")) {
      return new OPPCopyObjectProcessInstance();
    } else if (name.equalsIgnoreCase("Read Text File")) {
      return new OPPReadTextFileProcessInstance();
    } else if (name.equalsIgnoreCase("Transform JSON String To Object")) {
      return new OPPTransformJSONStringToObjectProcessInstance();
    }
    return null;
  }
}
