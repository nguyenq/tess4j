## Tess4J

A Java JNA wrapper for [Tesseract OCR API](http://code.google.com/p/tesseract-ocr/).

Tess4J is released and distributed under the [Apache License, v2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Features

The library provides optical character recognition (OCR) support for:

* TIFF, JPEG, GIF, PNG, and BMP image formats
* Multi-page TIFF images
* PDF document format

## Maven Dependency

**Releases**
```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>1.3.0</version>
</dependency>
```

**Snapshots**
```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>X.X.X-SNAPSHOT</version>
</dependency>
```
>SNAPSHOT versions are not synchronized to the Central Repository. If you wish your users to consume your SNAPSHOT versions, they would need to add the snapshot repository to their Nexus, settings.xml, or pom.xml. Successfully deployed SNAPSHOT versions will be found in [https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/) Source: [http://central.sonatype.org/pages/apache-maven.html#performing-a-snapshot-deployment](http://central.sonatype.org/pages/apache-maven.html#performing-a-snapshot-deployment)

So go ahead and add this to your pom.xml:
```xml
<profiles>
	<profile>
		<id>allow-snapshots</id>
		<activation>
			<activeByDefault>true</activeByDefault>
		</activation>
		<repositories>
			<repository>
				<id>snapshots-repo</id>
				<url>https://oss.sonatype.org/content/repositories/snapshots</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>true</enabled>
				</snapshots>
			</repository>
		</repositories>
	</profile>
</profiles>
```

## Tutorial

[Development with Tess4J in NetBeans, Eclipse, and Command-line](http://tess4j.sourceforge.net/tutorial/)
