package blue.sparse.robots.util;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public final class Skin {

	private static final String SKIN_DATA_DOWNLOAD_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

	public static Skin fromFile(File file) throws IOException {
		final List<String> lines = Files.readAllLines(file.toPath());
		return new Skin(lines.get(0), lines.get(1));
	}

	public final String value;
	public final String signature;

	public Skin(String value, String signature) {
		this.value = value;
		this.signature = signature;
	}

	public ItemStack createHead() {
		return null;
	}
}
