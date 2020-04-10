package wife.heartcough;

import org.apache.commons.lang3.StringUtils;

public class Test {
	public static void main(String[] args) {
		String path = "C:/a/b/";
		System.out.println(StringUtils.split(path, "/").length);
	}
}
