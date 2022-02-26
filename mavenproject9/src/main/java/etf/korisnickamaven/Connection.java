package etf.korisnickamaven;

import entiteti.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class Connection {
	
	static final Retrofit rf = new Retrofit.Builder().baseUrl("http://localhost:8080/server/a/").addConverterFactory(GsonConverterFactory.create()).build();
	static final Server s = rf.create(Server.class);
	
	public interface Server {
		@POST("p1/mesto")
		Call<String> postMesto(@Query("naziv") String naziv, @Query("postBr") String postBr);
		
		@POST("p1/filijala")
		Call<String> postFilijala(@Query("idMes") String idMes, @Query("naziv") String naziv, @Query("adresa") String adresa);
		
		@POST("p1/komitent")
		Call<String> postKomitent(@Query("naziv") String naziv, @Query("adresa") String adresa, @Query("sediste") String sediste);
		
		@PUT("p1/komitent")
		Call<String> putKomitent(@Query("idK") String idK, @Query("idMes") String idM);
		
		@POST("p2/racun")
		Call<String> postRacun(@Query("idK") String idK, @Query("idFil") String idFil, @Query("minus") String minus);
		
		@DELETE("p2/racun")
		Call<String> deleteRacun(@Query("idRac") String idRac);
		
		@POST("p2/stavka")
		Call<String> postStavka(@Query("tip") String tip, @Query("iznos") String iznos, @Query("idRac1") String idRac1, @Query("random") String random);
		
		@GET("p1/mesto")
		Call<List<Mesto>> getMesto();
		
		@GET("p1/filijala")
		Call<List<Filijala>> getFilijala();
		
		@GET("p1/komitent")
		Call<List<Komitent>> getKomitent();
		
		@GET("p2/racun")
		Call<Racun> getRacun(@Query("idK") String idK);
		
		@GET("p2/stavka")
		Call<Stavka> getStavka(@Query("idRac") String idRac);
		
		@GET("p3/all")
		Call<ArrayList<List>> getAll(@Query("podsistem") String podsistem);
		
		@GET("p3/difference")
		Call<List<Object>> getDifference();
	}



	public static Object recieve(String what, String... values) throws IOException, Exception {
		Response r;
		switch (what) {
			case ("CreateMesto"):
				if ((r = s.postMesto(values[0], values[1]).execute()).code() != 200) throw new Exception("Nepoznata greska!");
				break;
			case ("CreateFilijala"):
				if ((r = s.postFilijala(values[0], values[1], values[2]).execute()).code() != 200) throw new Exception("Nepoznata greska!");
				break;
			case ("CreateKomitent"):
				return s.postKomitent(values[0], values[1], values[2]).execute().body();
			case ("ChangeKomitentSediste"):
				return s.putKomitent(values[0], values[1]).execute().body();
			case ("OpenRacun"):
				return s.postRacun(values[0], values[1], values[2]).execute().body();
			case ("CloseRacun"):
				return s.deleteRacun(values[0]).execute().body();
			case ("CreateStavka"):
				return s.postStavka(values[0], values[1],values[2],values[3]).execute().body();
			case ("GetMestoAll"):
				Response<List<Mesto>> m = s.getMesto().execute();
				
				return m.body();
			case ("GetFilijalaAll"):
				return s.getFilijala().execute().body();
			case ("GetKomitentAll"):
				return s.getKomitent().execute().body();
			case ("GetRacunKomitent"):
				return s.getRacun(values[0]);
			case ("GetStavkaRacun"):
				return s.getStavka(values[0]);
			case ("GetAll"):
				return s.getAll(values[0]).execute().body();
			case ("GetDiff"):
				return s.getDifference().execute().body();
			default:
				return null;
		}
		
		return r.body();
	}
}
