package uk.ac.cam.cl.retailcategorymapper.classifier.normalizer;

import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import java.text.Normalizer;
import java.lang.Character;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductNormalizer {
	private static List<String> STOP_WORDS = Arrays.asList("a", "an", "and", "are", "as", "at",
			"be", "by",	"do", "don't", "for", "from", "has", "in", "is", "it", "its", "of", "on",
			"that", "the", "this", "to", "was", "were", "will", "with");

	public static Product normalizeProduct(Product inputProduct) {
		ProductBuilder builder = new ProductBuilder();
		builder.setId(inputProduct.getId());
		builder.setName(normalizeString(inputProduct.getName()));
		builder.setDescription(normalizeString(inputProduct.getDescription()));
		builder.setPrice(inputProduct.getPrice());
		builder.setOriginalCategory(inputProduct.getOriginalCategory());
		for (String attributeName: inputProduct.getAttributeNames()) {
			builder.addAttribute(attributeName, inputProduct.getAttribute(attributeName));
		}
		return builder.createProduct();
	}

	private static String normalizeString(String inputString) {
		List<String> words = Arrays.asList(inputString.split("\\s+"));
		words = words.stream()
				.map(w -> normalizeWord(w))
				.filter(w -> w.length() > 1 && !w.matches("^\\d+$"))
				.collect(Collectors.toList());
		words = Arrays.asList(collapseSpaceRuns(String.join(" ", words)).split("\\s+"));
		words = words.stream()
				.filter(w -> !STOP_WORDS.contains(w))
				.collect(Collectors.toList());
		return String.join(" ", words);
	}

	private static int filterSpaces(int codePoint) {
		if (Character.isWhitespace(codePoint)) {
			return Character.codePointAt(" ", 0);
		} else if (codePoint == Character.codePointAt("'", 0)) {
			return codePoint;
		} else {
			switch (Character.getType(codePoint)) {
				case Character.INITIAL_QUOTE_PUNCTUATION:
				case Character.FINAL_QUOTE_PUNCTUATION:
					return Character.codePointAt("'", 0);
				case Character.CONTROL:
				case Character.FORMAT:
				case Character.LINE_SEPARATOR:
				case Character.PARAGRAPH_SEPARATOR:
				case Character.SPACE_SEPARATOR:
				case Character.CONNECTOR_PUNCTUATION:
				case Character.DASH_PUNCTUATION:
				case Character.START_PUNCTUATION:
				case Character.END_PUNCTUATION:
				case Character.OTHER_PUNCTUATION:
				case Character.CURRENCY_SYMBOL:
				case Character.MATH_SYMBOL:
				case Character.MODIFIER_SYMBOL:
				case Character.OTHER_SYMBOL:
					return Character.codePointAt(" ", 0);
				default:
					return codePoint;
			}
		}
	}

	private static boolean isUnwantedCodePoint(int codePoint) {
		switch (Character.getType(codePoint)) {
			case Character.LOWERCASE_LETTER:
			case Character.UPPERCASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.TITLECASE_LETTER:
			case Character.DECIMAL_DIGIT_NUMBER:
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
			case Character.DASH_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.CURRENCY_SYMBOL:
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				return false;
			default:
				return true;
		}
	}


	private static String collapseSpaceRuns(String word) {
		return word.replaceAll("\\s+", " ");
	}

	private static String unCamelCaseIfy(String word) {
		return word.replaceAll("(((?<=[a-z])[A-Z])|([A-Z](?![A-Z]|$)))", " $1")
				.trim()
				.toLowerCase();
	}

	private static String normalizeWord(String inputWord) {
		/*
			NFKD means Normal Form Decomposed, with kompatibility kharacters replaced.
		    So combined characters like 'ü' are encoded 'u <combining umlaut>'
		    and kompatibility kharacters which are synonyms of other characters
		    are replaced by the canonical one. e.g. '<Roman Numeral One>' -> 'I'.
		 */
		String word = unCamelCaseIfy(Normalizer.normalize(inputWord, Normalizer.Form.NFKD));
		final StringBuilder b = new StringBuilder();
		word.codePoints()
		.filter(codePoint -> !isUnwantedCodePoint(codePoint))
		.map(codePoint -> filterSpaces(codePoint))
		.forEach(codePoint -> b.appendCodePoint(codePoint));
		word = b.toString().replaceAll("( '|' )", "");
		return Normalizer.normalize(collapseSpaceRuns(word), Normalizer.Form.NFC);
	}

	public static void main(String[] args) {
		String input = String.join(" ", args);
		System.out.println(normalizeString(input));
	}
}
