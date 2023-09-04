package com.inkus.infomancerforge.storage;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.inkus.infomancerforge.ImageUtilities;

public class JsonColor extends TypeAdapter<Color> {

	public JsonColor() {
	}

	@Override
	public void write(JsonWriter out, Color value) throws IOException {
		out.value(ImageUtilities.colorRGBAToHex(value));
	}

	@Override
	public Color read(JsonReader in) throws IOException {
		return ImageUtilities.hexToColor(in.nextString());
	}

}
