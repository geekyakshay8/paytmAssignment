package com.paytm.movies;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.paytm.MoviesDetails;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import restapiautomation.GetPropertiesValues;


public class PaytmMoviesTest {
    private Response response;
    MoviesDetails moviesDetails;

    @BeforeSuite
    public void initElement() throws IOException {
        GetPropertiesValues.configFileReader();
    }

    @BeforeClass
    public void initTestCase() {
        RestAssured.baseURI = GetPropertiesValues.properties.getProperty("baseURI");
        response = given()
                .when().get("/v2/movies/upcoming")
                .then().assertThat().log().all().statusCode(200).extract().response();
        moviesDetails = response.getBody().as(MoviesDetails.class);
    }

    @Test
    public void upcomingMoviesReleaseDateTest() throws IOException {
        Assert.assertEquals(response.getStatusCode(), 200);
        Date todayDate = new Date();
        moviesDetails.getUpcomingMovieData().forEach(i -> {
                    if (i.getReleaseDate() != null)
                        Assert.assertTrue(i.getReleaseDate().after(todayDate), i.getReleaseDate().toString() + "is before today's date " + todayDate);
                    else {
                        throw new AssertionError("movie " + i.getMovieName() + " release date is null.");
                    }
                }
        );
    }

    @Test
    public void upcomingMovePosterURLTest() throws IOException {
        moviesDetails.getUpcomingMovieData().forEach(i -> {
                    if (i.getMoviePosterUrl() != null)
                        Assert.assertTrue(Pattern.matches(".*jpg$", i.getMoviePosterUrl()), i.getMovieName() + " does not have  .jpg poster \n" + i.getMoviePosterUrl());

                    else {
                        throw new AssertionError("movie " + i.getMovieName() + " MoviePosterUrl date is null.");
                    }
                }
        );
    }

    @Test(description = "")
    public void upcomingMovieUniqueCode() {
        List<String> code = new ArrayList<>();

        moviesDetails.getUpcomingMovieData().forEach(i -> {
                    if (i.getMoviePosterUrl() != null)
                        code.add(i.getPaytmMovieCode());
                    else {
                        throw new AssertionError("movie " + i.getMovieName() + " MoviePosterUrl date is null.");
                    }
                }
        );
        Set<String> s = new HashSet<String>(code);
        Assert.assertEquals(s.size(), code.size(), "code is not unique");
    }

    @Test(description = "")
    public void upcomingMovieLanguageTest() {

        moviesDetails.getUpcomingMovieData().forEach(i -> {
                    if (i.getLanguage() != null) {
                        String[] languageArr = i.getLanguage().split(",");
                        Assert.assertEquals(languageArr.length, 1, "more than 1 language " + Arrays.toString(languageArr));
                    } else {
                        throw new AssertionError("movie " + i.getMovieName() + " language is null.");
                    }
                }
        );
    }

    @Test(description = "")
    public void upcomingMoviecontetXLTest() {
        List<String> moviesNames = new ArrayList<>();
        moviesDetails.getUpcomingMovieData().forEach(i -> {
                    if (i.getIsContentAvailable() != null && i.getIsContentAvailable() == 0) {
                        moviesNames.add(i.getMovieName());
                    }
                }
        );
        if (moviesNames.size() > 0) {
            createExelFile(moviesNames);
        }
    }

    public void createExelFile(List<String> moviesName) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("MoviesName");
        AtomicInteger rowConter = new AtomicInteger();
        moviesName.forEach(i -> {
            sheet.createRow(rowConter.get()).createCell(0).setCellValue(i);
            rowConter.getAndIncrement();
        });
        try (FileOutputStream out = new FileOutputStream(new File("moviesName.xlsx"))) {
            //Write the workbook in file system
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
