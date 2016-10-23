package arcatch.dsl.rule.onlyCan;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class OnlyCanHandle extends DesignRule {

	public OnlyCanHandle() {
		super();
	}

	public OnlyCanHandle(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("only (");
		template.append(this.getModule().getLabel());
		template.append(") can handle (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>only M can handle E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean onlyCanHanlde = true;

		Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		Set<ClassNode> systemClasses = CheckerUtil.getAllSystemClasses();

		systemClasses.removeAll(moduleClasses);

		Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode exceptionClass : exceptionClasses) {
			for (ClassNode systemClass : systemClasses) {
				for (MethodNode method : systemClass.getAllMethods()) {
					if (CheckerUtil.handles(method, exceptionClass)) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());
						violation.setExceptionName(exceptionClass.getClassName());
						this.addViolation(violation);
						onlyCanHanlde = false;
					}
				}
			}
		}

		return onlyCanHanlde;

	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is handling the exception [");
			text.append(violation.getExceptionName());
			text.append("]\n");
		}
		return text.toString();
	}

}
