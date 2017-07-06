package uk.co.boots.columbus.cmdb.cli;

import org.springframework.boot.ExitCodeGenerator;

public class ExceptionWithExitCode extends RuntimeException implements ExitCodeGenerator {
    private int exitCode;
    
	public ExceptionWithExitCode(String message, int exitCode) {
        super(message);
    }

    public int getExitCode() {
        return exitCode;
    }
}