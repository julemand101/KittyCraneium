package codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import contextual.exceptions.IdentException;

import syntactic.exceptions.CompileException;
import ast.IAST;

public interface ICodeGenerator {
	public void generateCode(IAST program, File outputDirectory) throws FileNotFoundException, IOException, CompileException, IdentException;
}
