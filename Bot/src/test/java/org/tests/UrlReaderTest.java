package org.tests;

import static org.junit.Assert.assertTrue;

import bot.reddit.utils.JsonGateway;
import bot.reddit.exceptions.UrlException;
import com.google.gson.JsonObject;
import org.junit.Test;

public class UrlReaderTest
{
    @Test(expected = UrlException.class)
    public void getJsonFromUrl_INVALID() throws UrlException {
        //Arrange
        String url = "https://www.reddit.com/";

        //Act
        JsonObject result = JsonGateway.getJsonObjectFromUrl(url);
    }
}
