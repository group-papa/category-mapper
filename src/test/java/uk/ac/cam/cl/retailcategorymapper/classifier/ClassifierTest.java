package uk.ac.cam.cl.retailcategorymapper.classifier;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.classifier.features.BasicFeatureConverter;
import uk.ac.cam.cl.retailcategorymapper.entities.FeatureSource;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import java.util.List;

/**
 * Created by tatsianaivonchyk on 2/11/15.
 */
public class ClassifierTest {
    private static NaiveBayesClassifier nbc1 = new NaiveBayesClassifier();
    private static NaiveBayesClassifier nbc2 = new NaiveBayesClassifier();
    private static NaiveBayesClassifier nbc3 = new NaiveBayesClassifier();

    private static String[] c1Array = {"cat", "subcat", "subsubcat"};
    private static Category c1 = (new CategoryBuilder()).setId("catID").setParts(c1Array).createCategory();
    private static Category c2 = (new CategoryBuilder()).setId("catID2").setParts(c1Array).createCategory();


    private static Product p = (new ProductBuilder()).setId("prodID").setName("part1 part2")
            .setDescription("description p").setPrice(100).setOriginalCategory(c1).createProduct();
    private static Product p2 = (new ProductBuilder()).setId("prodID2")
            .setName("product two").setDescription("p2").setPrice(200).createProduct();
    private static Product p3 = (new ProductBuilder()).setId("prodID3").setName("part2 three")
            .setDescription("p3").setPrice(300).setOriginalCategory(c1).createProduct();

    private static BasicFeatureConverter fc1 = new BasicFeatureConverter();

    private static Feature pName1 = new Feature(FeatureSource.NAME, "part1");
    private static Feature pName2 = new Feature(FeatureSource.NAME, "part2");
    private static Feature pDesc1 = new Feature(FeatureSource.DESCRIPTION, "description");
    private static Feature pDesc2 = new Feature(FeatureSource.DESCRIPTION, "p");
    private static Feature pPrice = new Feature(FeatureSource.PRICE, "100");
    private static Feature pCat1 = new Feature(FeatureSource.ORIGINAL_CATEGORY, "cat");
    private static Feature pCat2 = new Feature(FeatureSource.ORIGINAL_CATEGORY, "subcat");
    private static Feature pCat3 = new Feature(FeatureSource.ORIGINAL_CATEGORY, "subsubcat");

    @Test
    public void testFeatureConverter1() {
        List<Feature> featuresList = fc1.changeProductToFeature(p);

        Assert.assertFalse(featuresList.isEmpty());

        Assert.assertTrue(featuresList.contains(pName1) && featuresList.contains(pName2));
        Assert.assertTrue(featuresList.contains(pDesc1) && featuresList.contains(pDesc2));

        Assert.assertTrue(featuresList.contains(pPrice));

        Assert.assertTrue(featuresList.contains(pCat1) &&
                featuresList.contains(pCat2) && featuresList.contains(pCat3));
    }

    @Test
    public void testAddSeenFeatureInSpecifiedCategoryOneFeatureGetterMethods() {
        nbc1.addSeenFeatureInSpecifiedCategory(pName1, c1);
        Assert.assertTrue(nbc1.getFeatures().contains(pName1));
        Assert.assertTrue(nbc1.getCategories().contains(c1));
        Assert.assertTrue(nbc1.getTotalFeatureCounts().containsKey(pName1)
                && nbc1.getTotalFeatureCounts().containsValue(1));
        Assert.assertTrue(nbc1.getCategoryCounts().containsKey(c1)
                && nbc1.getCategoryCounts().containsValue(1));
        Assert.assertTrue(nbc1.getFeatureCountPerCategory().containsKey(c1));
        Assert.assertEquals(nbc1.getFeatureCountInCategory(pName1, c1), 1);
        Assert.assertEquals(nbc1.getFeatureProbabilityGivenCategory(pName2, c1), 0, 0);
        Assert.assertEquals(nbc1.getFeatureProbabilityGivenCategory(pName1, c1), 1, 0);
        Assert.assertEquals(nbc1.getTotalCategoriesSeen(), 1);
        Assert.assertEquals(nbc1.getCategoryProbability(c1), 1, 0);
        Assert.assertEquals(nbc1.getCategoryCount(c1), 1, 0);
    }

    @Test
    public void testTrainWithBagOfWordsSingleProductOneProduct() {
        nbc2.trainWithBagOfWordsSingleProduct(p, c1);
        // FIXME: These values are no longer applicable because of ngrams.
        //Assert.assertEquals(nbc2.getFeatures().size(), 8, 0);
        Assert.assertTrue(nbc2.getFeatures().contains(pName1));
        Assert.assertTrue(nbc2.getCategories().contains(c1));
        //Assert.assertEquals(nbc2.getTotalFeatureCounts().size(), 8, 0);
        Assert.assertTrue(nbc2.getTotalFeatureCounts().containsKey(pName1)
                && nbc1.getTotalFeatureCounts().containsValue(1));
        Assert.assertTrue(nbc2.getCategoryCounts().containsKey(c1)
                && nbc1.getCategoryCounts().containsValue(1));
        Assert.assertTrue(nbc2.getFeatureCountPerCategory().containsKey(c1));
        //Assert.assertEquals(nbc2.getFeatureCountPerCategory().get(c1).size(), 8);
        Assert.assertEquals(nbc2.getFeatureCountInCategory(pName1, c1), 1);
    }

    @Test
    public void testTrainWithBagOfWordsSingleProductTwoProducts() {
        nbc3.trainWithBagOfWordsSingleProduct(p, c2);
        nbc3.trainWithBagOfWordsSingleProduct(p3, c2);
        // FIXME: These values are no longer applicable because of ngrams.
        //Assert.assertEquals(nbc3.getFeatures().size(), 11, 0);
        Assert.assertTrue(nbc3.getFeatures().contains(pCat1));
        Assert.assertTrue(nbc3.getCategories().contains(c2));
        Assert.assertEquals(nbc3.getCategories().size(), 1);
        //Assert.assertEquals(nbc3.getTotalFeatureCounts().size(), 11, 0);
        Assert.assertTrue(nbc3.getTotalFeatureCounts().containsKey(pName1));
        Assert.assertTrue(nbc3.getTotalFeatureCounts().get(pCat1) == 2);
        Assert.assertTrue(nbc3.getCategoryCounts().containsKey(c2));
        //Assert.assertTrue(nbc3.getCategoryCounts().containsValue(15));
        Assert.assertTrue(nbc3.getFeatureCountPerCategory().containsKey(c2));
        Assert.assertTrue(nbc3.getFeatureCountPerCategory().get(c2).get(pName1) == 1);
        Assert.assertTrue(nbc3.getFeatureCountPerCategory().get(c2).get(pCat1) == 2);
        //Assert.assertEquals(nbc3.getFeatureCountPerCategory().get(c2).size(), 11);
        //Assert.assertEquals(nbc3.getFeatureCountInCategory(pName1, c2), 1);
        //Assert.assertEquals(nbc3.getFeatureCountInCategory(pCat1, c2), 2);
    }

}

