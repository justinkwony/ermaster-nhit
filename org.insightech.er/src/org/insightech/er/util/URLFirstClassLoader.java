package org.insightech.er.util;

import java.net.URL;
import java.net.URLClassLoader;

public class URLFirstClassLoader extends URLClassLoader {

	private ClassLoader parentClassLoader;

	public URLFirstClassLoader(URL[] paramArrayOfURL,
			ClassLoader paramClassLoader) {
		super(paramArrayOfURL);

		this.parentClassLoader = paramClassLoader;
	}

	@Override
	public URL getResource(String paramString) {
		URL url = super.getResource(paramString);

		if (url == null) {
			url = parentClassLoader.getResource(paramString);
		}

		return url;
	}

}
