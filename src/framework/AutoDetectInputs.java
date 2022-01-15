/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.HashMap;

/**
 *
 * @author truep
 */
public class AutoDetectInputs
{
    static String[] firstNameVariations = { "namefirst", "first", "firstname", "fname", "first name", "f name", "first_name", "fn", "firstnm" };

    static String[] lastNameVariations = { "namelast", "last", "lastname", "lname", "last name", "l name", "last_name", "ln", "lastnm" };

    static String[] fullNameVariations = { "namefull", "fullname", "name", "full name", "contact", "name1", "name 1" };

    static String[] addressVariations = { "a1", "addressline1", "address", "street", "addressline1", "address line1", "address 1", "address1", "street address", "address_1", "delivery address", "str1", "add1", "addr1", "business address", "add 1", "address line 1" };

    static String[] address2Variations = { "a2", "addressline2", "address2", "addressline2", "address line2", "street2", "street 2", "address 2", "address_2", "str2", "add2", "add 2", "addr2", "address line 2" };

    static String[] cityVariations = { "city", "town" };

    static String[] stateVariations = { "state", "st", "province", "prov" };

    static String[] zipVariations = { "postal", "zip", "zipcode", "postalcode", "postal_code", "postal code", "post_code", "postcode", "post code" };

    static String[] companyVariations = { "company", "business", "organization", "organization name" };

    static String[] suiteVariations = { "suite" };

    static String[] plus4Variations = { "+4", "plus4", "plus 4" };

    static String[] recordIDVariations = { "recordid", "recnum", "recno", "recid", "id", "rec" };

    static String[] phoneVariations = { "phone", "phonenumber", "fone", "fonenumber", "phonenum", "fonenum" };

    static String[] emailVariations = { "email", "emailaddress", "emailaddr", "e-mail" };

    //This relates input column names to the variation lists
    //Add input columns from services here in all lower case as the key, then the variation list as the value
    public static HashMap<String, String[]> variationDictionary = new HashMap<String, String[]>();
    static {
        variationDictionary.put("namefirst", firstNameVariations);
        variationDictionary.put("namelast", lastNameVariations);
        variationDictionary.put("namefull", fullNameVariations);
        variationDictionary.put("addressline1", addressVariations);
        variationDictionary.put("addressline2", address2Variations);
        variationDictionary.put("city", cityVariations);
        variationDictionary.put("state", stateVariations);
        variationDictionary.put("postalcode", zipVariations);
        variationDictionary.put("company", companyVariations);
        variationDictionary.put("suite", suiteVariations);
        variationDictionary.put("plus4", plus4Variations);
        variationDictionary.put("firstname", firstNameVariations);
        variationDictionary.put("lastname", lastNameVariations);
        variationDictionary.put("fullname", fullNameVariations);
        variationDictionary.put("companyname", companyVariations);
        variationDictionary.put("recordid", recordIDVariations);
        variationDictionary.put("emailaddress", emailVariations);
        variationDictionary.put("phonenumber", phoneVariations);
        variationDictionary.put("a1", addressVariations);
        variationDictionary.put("a2", address2Variations);
        variationDictionary.put("postal", zipVariations);
    };
}
