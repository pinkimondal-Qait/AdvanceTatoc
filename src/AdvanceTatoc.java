import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class AdvanceTatoc {

	public static void main(String[] args) throws InterruptedException, IOException{
		WebDriver driver =new ChromeDriver();
		driver.get("http://10.0.1.86/tatoc/");
		driver.findElement(By.linkText("Advanced Course")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/span[1]")).click();
		driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/span[5]")).click();
		String symbol=driver.findElement(By.cssSelector("div#symboldisplay")).getText();
	    System.out.println(symbol);
	   try{  
	    	Class.forName("com.mysql.jdbc.Driver");  
	    	Connection con=(Connection) DriverManager.getConnection("jdbc:mysql://10.0.1.86/tatoc","tatocuser","tatoc01");  
	    	
	    	Statement stmt=(Statement) con.createStatement();  
	    	  
	    	ResultSet rs=(ResultSet) stmt.executeQuery("select name, passkey from identity i,credentials c where i.symbol='"+symbol+"' AND i.id=c.id");  
	    	
	    	while(rs.next()) { 
	    	System.out.println(rs.getString(1)+"  "+rs.getString(2));  
	    	String user= rs.getString(1);
	    	driver.findElement(By.id("name")).sendKeys(user);
	    	String passkey= rs.getString(2);
	    	driver.findElement(By.id("passkey")).sendKeys(passkey);
	    	driver.findElement(By.id("submit")).click();
	    	break;
	    	}
	       	con.close();  
	    }catch(Exception e){ System.out.println(e);
	   } 
	 	   
	   JavascriptExecutor js = (JavascriptExecutor) driver;
	   Thread.sleep(5000);
	   js.executeScript("document.getElementsByClassName('video')[0].getElementsByTagName('object')[0].playMovie();");
	   Thread.sleep(25000);
	   driver.findElement(By.linkText("Proceed")).click();
 
      driver.get("http://10.0.1.86/tatoc/advanced/rest"); 
	  String session_id=driver.findElement(By.id("session_id")).getText();
	  String[] session=session_id.split(":");
	  System.out.println(session[1].trim()+"  ");
	  
	  
	  //GET Calling
	  String get_url="http://10.0.1.86/tatoc/advanced/rest/service/token/"+session[1].trim();
	  URL url = new URL(get_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String output;
		System.out.println("Output from Server .... \n");
		StringBuilder sb = new StringBuilder();
		while ((output = br.readLine()) != null) {
			System.out.println(output);
			sb.append(output);
		}
		String json = sb.toString();
		JsonParser parser = new JsonParser();
		JsonObject myobject = (JsonObject)parser.parse(json);
		//Accessing the value of "token"
		System.out.println(myobject.get("token"));
		JsonElement tokenV=myobject.get("token");
		System.out.println(tokenV.toString());
		conn.disconnect();

		//POST calling	
      String post_url="http://10.0.1.86/tatoc/advanced/rest/service/register";
      String urlParameters = "id="+session[1].trim()+"&signature="+tokenV.toString().substring(1, tokenV.getAsString().length()-1)+"&allow_access=1";
      URL url2 = new URL(post_url);
      HttpURLConnection conn1 = (HttpURLConnection)url2.openConnection();
      conn1.setRequestMethod("POST");
      conn1.setDoOutput(true);
      conn1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
	  wr.close();
  	  int responseCode = conn1.getResponseCode();
	  System.out.println("\nSending 'POST' request to URL : " + url);
	  System.out.println("Post parameters : " + urlParameters);
	  System.out.println("Response Code : " + responseCode);
	  //  driver.findElement(By.cssSelector(".page a")).click();
      driver.findElement(By.linkText("Proceed")).click();
      conn1.disconnect();
	  Thread.sleep(6000);
      driver.navigate().back();
	  //driver.findElement(By.linkText("Download File")).click();
   	    Thread.sleep(5000);
      //	driver.get("http://10.0.1.86/tatoc/advanced/file/handle"); 
     	driver.findElement(By.linkText("Download File")).click();
		Thread.sleep(5000);
       	BufferedReader br2 = null;
		List<String> strings=null;
		try {
			String s1;
			br2 = new BufferedReader(new FileReader(System.getProperty("user.home") +"//Downloads"+"//file_handle_test.dat"));
			strings= new ArrayList<String>();
			while ((s1 = br2.readLine()) != null) 
			{
				strings.add(s1);
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (br2 != null)
					br2.close();
			} 
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	    String signature= strings.get(2);
	    String[] sign= signature.split(": ");
	    driver.findElement(By.cssSelector("#signature")).sendKeys(sign[1]);
	    driver.findElement(By.cssSelector(".submit")).click();
}
}


