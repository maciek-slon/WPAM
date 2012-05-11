package edu.wut.wpam.runwithme;

public class RunAppContext {
	private static RunAppContext runAppContext = null;

	private RunAppContext() {
	}

	public static RunAppContext instance() {
		if (runAppContext == null) { 
			runAppContext = new RunAppContext(); 
		} 
		return runAppContext;
	}
}
