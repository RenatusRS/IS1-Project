package app;

import entiteti.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.ScalarDataModel;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;

public class Connection {

	static final Retrofit rf = new Retrofit.Builder().baseUrl("http://localhost:8080/server/a/").addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
	static final Server s = rf.create(Server.class);

	public interface Server {

		@POST("p1/mesto")
		Call<String> postMesto(@Query("naziv") String naziv, @Query("postBr") String postBr);

		@POST("p1/filijala")
		Call<String> postFilijala(@Query("idMes") String idMes, @Query("naziv") String naziv, @Query("adresa") String adresa);

		@POST("p1/komitent")
		Call<String> postKomitent(@Query("naziv") String naziv, @Query("adresa") String adresa, @Query("sediste") String sediste);

		@PUT("p1/komitent")
		Call<String> putKomitent(@Query("idKom") String idK, @Query("idMes") String idM);

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
		Call<List<Racun>> getRacun(@Query("idK") String idK);

		@GET("p2/stavka")
		Call<List<Stavka>> getStavka(@Query("idRac") String idRac);

		@GET("p3/all")
		Call<String> getAll();

		@GET("p3/difference")
		Call<String> getDifference();
	}

	public static Object recieve(String what, String... values) throws IOException, Exception {
		Response r;
		switch (what) {
			case ("CreateMesto"):
				r = s.postMesto(values[0], values[1]).execute();
				break;
			case ("CreateFilijala"):
				r = s.postFilijala(values[0], values[1], values[2]).execute();
				break;
			case ("CreateKomitent"):
				r = s.postKomitent(values[0], values[1], values[2]).execute();
				break;
			case ("ChangeKomitentSediste"):
				r = s.putKomitent(values[0], values[1]).execute();
				break;
			case ("OpenRacun"):
				r = s.postRacun(values[0], values[1], values[2]).execute();
				break;
			case ("CloseRacun"):
				r = s.deleteRacun(values[0]).execute();
				break;
			case ("CreateStavka"):
				r = s.postStavka(values[0], values[1], values[2], values[3]).execute();
				break;
			case ("GetMestoAll"):
				r = s.getMesto().execute();
				break;
			case ("GetFilijalaAll"):
				r = s.getFilijala().execute();
				break;
			case ("GetKomitentAll"):
				r = s.getKomitent().execute();
				break;
			case ("GetRacunKomitent"):
				r = s.getRacun(values[0]).execute();
				break;
			case ("GetStavkaRacun"):
				r = s.getStavka(values[0]).execute();
				break;
			case ("GetAll"):
				r = s.getAll().execute();
				break;
			case ("GetDiff"):
				r = s.getDifference().execute();
				break;
			default:
				return null;
		}

		switch (r.code()) {
			case (400):
				throw new Exception("Istekla veza!");
			case (401):
				throw new Exception("Mesto ne postoji!");
			case (402):
				throw new Exception("Komitent ne postoji!");
			case (403):
				throw new Exception("Filijala ne postoji");
			case (404):
				throw new Exception("Racun 1 ne postoji");
			case (405):
				throw new Exception("Racun 1 je zatvoren");
			case (406):
				throw new Exception("Racun 2 ne postoji");
			case (408):
				throw new Exception("Racun 1 je blokiran");
			case (409):
				throw new Exception("Racun 2 je zatvoren");
			case (200):
				break;
			default:
				throw new Exception("Nepoznata greska");
		}

		return r.body();
	}
}
