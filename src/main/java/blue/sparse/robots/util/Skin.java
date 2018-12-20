package blue.sparse.robots.util;

import blue.sparse.robots.version.VersionAdapter;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public final class Skin {

	public static Skin fromFile(File file) throws IOException {
		final List<String> lines = Files.readAllLines(file.toPath());
		return new Skin(lines.get(0), lines.get(1));
	}

	public static Skin fromInputStream(InputStream input) throws IOException {
		final List<String> lines = IOUtils.readLines(input);
		input.close();
		return new Skin(lines.get(0), lines.get(1));
	}

	public static Skin fromPlayer(Player player) {
		return VersionAdapter.getInstance().getSkin(player);
	}

	public final String value;
	public final String signature;

	public Skin(String value, String signature) {
		this.value = value;
		this.signature = signature;
	}

	public void writeToFile(File file) throws IOException {
		final BufferedWriter writer = Files.newBufferedWriter(file.toPath());
		writer.write(value + "\n" + signature);
		writer.close();
	}

	public ItemStack createHead() {
		return VersionAdapter.getInstance().createHead(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Skin)) return false;
		Skin skin = (Skin) o;
		return Objects.equals(value, skin.value) &&
				Objects.equals(signature, skin.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, signature);
	}

	@Override
	public String toString() {
		return "Skin{" +
				"value='" + value + '\'' +
				", signature='" + signature + '\'' +
				'}';
	}
}
