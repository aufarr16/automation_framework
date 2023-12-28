package automation_framework_test;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;

public class testscript {
    @Test
    public void testGetListUsersPos(){
        RestAssured.baseURI = "https://reqres.in";

        int pageNumber = 2; //test value boundary dari page, karena page hanya ada 2 kita tes page 2
        File jsonschema = new File("src/test/resources/json_schema/get_list_users.json");

        RestAssured
                .given().when()
                .get("/api/users?page=" + pageNumber)
                .then()
                .log().all()                                     //munculin datanya di terminal
                .assertThat().statusCode(200)   //cek status codenya 200 atau gak
                .assertThat().body("page", Matchers.equalTo(pageNumber))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
    }

    @Test
    public void testGetListUsersNeg(){
        RestAssured.baseURI = "https://reqres.in";

        int pageNumber = 3; //test value out of bound dari page > 2 untuk case negatif
        File jsonschema = new File("src/test/resources/json_schema/get_list_users.json");

        RestAssured
                .given().when()
                .get("/api/users?page=" + pageNumber)
                .then()
                .log().all()
                .assertThat().statusCode(400)   //harusnya kalo gagal kena 40x, cuma di reqres tidak ada validasi kelihatannya
                .assertThat().body("page", Matchers.equalTo(pageNumber))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
    }

    @Test
     public void testPostCreatePos(){
        RestAssured.baseURI = "https://reqres.in";

        String name = "Aufar";
        String job = "QA_Engineer";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("job", job);

        File jsonschema = new File("src/test/resources/json_schema/create_user.json");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("/api/users")
                .then().log().all()
                .assertThat().statusCode(201)
                .assertThat().body("name", Matchers.equalTo(name))
                .assertThat().body("job", Matchers.equalTo(job))
                .assertThat().body("$", Matchers.hasKey("id"))
                .assertThat().body("$", Matchers.hasKey("createdAt"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
    }

    @Test
    public void testPostCreateNeg(){
        RestAssured.baseURI = "https://reqres.in";

        int name = 123;     //set value yang tidak sesuai dengan seharusnya untuk case negatif. Seharusnya name tipe datanya String, kita buat integer
        String job = "QA_Engineer";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("job", job);

        RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("/api/users")
                .then().log().all()
                .assertThat().statusCode(400)       //harusnya kalo gagal kena 40x, cuma di reqres tidak ada validasi kelihatannya
                .assertThat().body("name", Matchers.equalTo(name))
                .assertThat().body("job", Matchers.equalTo(job))
                .assertThat().body("$", Matchers.hasKey("id"))
                .assertThat().body("$", Matchers.hasKey("createdAt"));
    }

    @Test
    public void testPutUpdatePos(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 5;
        String newName = "Morris";
        String newEmail = "jyo@mail.com";

        String email = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.email");
        String first_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.first_name");
        String last_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.last_name");
        String avatar = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.avatar");

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("id", user_id);
        newData.put("email", newEmail);
        newData.put("first_name", newName);
        newData.put("last_name", last_name);
        newData.put("avatar", avatar);

        JSONObject jsonData = new JSONObject(newData);

        File jsonschema = new File("src/test/resources/json_schema/put_update.json");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonData.toString())
                .when()
                .put("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("first_name", Matchers.equalTo(newName))
                .assertThat().body("email", Matchers.equalTo(newEmail))
                .assertThat().body("$", Matchers.hasKey("updatedAt"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
    }

    @Test
    public void testPutUpdateNeg(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 5;
        int newName = 321;  //set value yang tidak sesuai dengan seharusnya untuk case negatif. Seharusnya name tipe datanya String, kita buat integer
        String newEmail = "jyo@mail.com";

        String email = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.email");
        String first_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.first_name");
        String last_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.last_name");
        String avatar = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.avatar");

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("id", user_id);
        newData.put("email", newEmail);
        newData.put("first_name", newName);
        newData.put("last_name", last_name);
        newData.put("avatar", avatar);

        JSONObject jsonData = new JSONObject(newData);

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonData.toString())
                .when()
                .put("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(400)   //harusnya kalo gagal kena 40x, cuma di reqres tidak ada validasi kelihatannya
                .assertThat().body("first_name", Matchers.equalTo(newName))
                .assertThat().body("email", Matchers.equalTo(newEmail))
                .assertThat().body("$", Matchers.hasKey("updatedAt"));
    }

    @Test
    public void testPatchUpdatePos(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 5;
        String newFirstName = "Jyo";
        String newLastName = "_21";

        String first_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.first_name");
        String last_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.last_name");

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("first_name", newFirstName);
        newData.put("last_name", newLastName);

        JSONObject jsonData = new JSONObject(newData);

        File jsonschema = new File("src/test/resources/json_schema/patch_update.json");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonData.toString())
                .when()
                .patch("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("first_name", Matchers.equalTo(newFirstName))
                .assertThat().body("last_name", Matchers.equalTo(newLastName))
                .assertThat().body("$", Matchers.hasKey("updatedAt"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
    }

    @Test
    public void testPatchUpdateNeg(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 5;
        String newFirstName = "Jyo";
        int newLastName = 21;   //set value yang tidak sesuai dengan seharusnya untuk case negatif. Seharusnya name tipe datanya String, kita buat integer\

        String first_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.first_name");
        String last_name = RestAssured.given().when().get("/api/users/" + user_id).getBody().jsonPath().get("data.last_name");

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("first_name", newFirstName);
        newData.put("last_name", newLastName);

        JSONObject jsonData = new JSONObject(newData);

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonData.toString())
                .when()
                .patch("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(400)   //harusnya kalo gagal kena 40x, cuma di reqres tidak ada validasi kelihatannya
                .assertThat().body("first_name", Matchers.equalTo(newFirstName))
                .assertThat().body("last_name", Matchers.equalTo(newLastName))
                .assertThat().body("$", Matchers.hasKey("updatedAt"));
    }

    @Test
    public void testDeleteDeletePos(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 12;   //set value dengan limit atasnya. id terakhir adalah 12

        RestAssured.given()
                .when()
                .delete("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(204);
    }

    @Test
    public void testDeleteDeleteNeg(){
        RestAssured.baseURI = "https://reqres.in";

        int user_id = 13;   //set value id > 12 untuk tes negatif user tidak ditemukan

        RestAssured.given()
                .when()
                .delete("/api/users/" + user_id)
                .then().log().all()
                .assertThat().statusCode(400);
    }
}
