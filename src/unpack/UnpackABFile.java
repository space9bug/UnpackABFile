package unpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nick.abe.Main;

public class UnpackABFile {

	public static int BUFFER_SIZE = 2048;

	public static void createDirectory(String outputDir, String subDir) {
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals(""))) {// 子目录不为空
			file = new File(outputDir + File.separator + subDir);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void main(String[] args) throws Exception {
		String abPath = args[0];
		String dbPath = args[1];
		String abPassword = args[2];
		String tempPath = dbPath + File.separator + "temp";
		createDirectory(tempPath, null);
		String[] arg = { "unpack", abPath, tempPath + File.separator + "soul.tar", abPassword };
		Main.main(arg);

		System.out.println(unTar(tempPath + File.separator + "soul.tar", dbPath));
		System.out.println("解压完成");
	}

	public static List<String> unTar(File tarFile, String destDir) throws Exception {
		if (StringUtils.isBlank(destDir)) {
			destDir = tarFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		return unTar(new FileInputStream(tarFile), destDir);
	}

	private static List<String> unTar(InputStream inputStream, String destDir) throws Exception {
		String match = "apps/cn.soulapp.android/f/";
		String match2 = "apps/cn.soulapp.android/f/easemobDB/[0-9]{0,}.db$";
		List<String> fileNames = new ArrayList<String>();
		TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream, BUFFER_SIZE);
		TarArchiveEntry entry = null;
		try {
			while ((entry = tarIn.getNextTarEntry()) != null) {
				fileNames.add(entry.getName());
				Pattern p = Pattern.compile(match2);
				Matcher m = p.matcher(entry.getName());
				if (m.matches()) {

					File tmpFile = new File(destDir + File.separator + entry.getName().substring(match.length()));
					createDirectory(tmpFile.getParent() + File.separator, null);// 创建输出目录

					OutputStream out = null;
					try {
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] b = new byte[2048];
						while ((length = tarIn.read(b)) != -1) {
							out.write(b, 0, length);
						}
					} finally {
						IOUtils.closeQuietly(out);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			IOUtils.closeQuietly(tarIn);
		}

		return fileNames;
	}

	public static List<String> unTar(String tarFile, String destDir) throws Exception {
		File file = new File(tarFile);
		return unTar(file, destDir);
	}
}