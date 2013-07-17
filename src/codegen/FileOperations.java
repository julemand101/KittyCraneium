package codegen;

import java.io.*;
import java.nio.channels.*;
import java.util.zip.*;

public class FileOperations {

	/*
	 * The purpose of this method is to copy a whole directory structure from source to target.
	 * 
	 * Source:
	 * http://www.techiegyan.com/?p=235
	 */

	// If targetLocation does not exist, it will be created.
	static public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists())
				targetLocation.mkdir();

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]),
						new File(targetLocation, children[i]));
			}
		} else
			copyFile(sourceLocation, targetLocation);
	}

	/*
	 * Copy sourceFile to destFile in an effective way.
	 * 
	 * Source:
	 * http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java 
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if(!destFile.exists())
			destFile.createNewFile();

		FileChannel source = null;
		FileChannel destination = null;
		
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null)
				source.close();

			if (destination != null)
				destination.close();
		}
	}
	
	public static void emptyDirectory(File directory) {
		for (File file : directory.listFiles())
		{
			if (file.isFile())
				file.delete();
			else if (file.isDirectory()) {
				emptyDirectory(file);
				file.delete();
			}
		}
	}
	
	public static void unzip(InputStream srcFile, File destDir) throws IOException {
		OutputStream output = null;
		ZipInputStream zipStream = new ZipInputStream(srcFile);
		ZipEntry entry;
		
		while ((entry = zipStream.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				new File(destDir, entry.getName()).mkdir();
				continue;
			}
			
			File destFile = new File(destDir, entry.getName());
			FileOutputStream fos = new FileOutputStream(destFile);
			output = new BufferedOutputStream(fos);
			copyStreams(zipStream, output);
			output.flush();
			output.close();
		}
		
		zipStream.close();
	}

	private static void copyStreams(ZipInputStream input, OutputStream output) throws IOException {
		int count;
		byte data[] = new byte[1024];
		
		while ((count = input.read(data, 0, 1024)) != -1) {
			output.write(data, 0, count);
		}
	}
}
