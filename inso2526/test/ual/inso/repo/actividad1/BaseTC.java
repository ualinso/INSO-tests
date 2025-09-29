package ual.inso.repo.actividad1;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class BaseTC {

	@Rule
	public TestWatcher watchman = new TestWatcher() {
		@Override
		public void starting(Description d) {
			System.out.println(String.format("'%s' running...", d.getClassName() + "_" + d.getMethodName()));
		}

		@Override
		public void failed(Throwable e, Description d) {
			System.out.println(String.format("'%s' failed...: %s", d.getClassName() + "_" + d.getMethodName(), e.getMessage()));
		}

		@Override
		public void finished(Description d) {
			System.out.println(String.format("'%s' finished...", d.getClassName() + "_" + d.getMethodName()));
		}
	};
}
