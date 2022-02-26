package entiteti;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "komitent")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Komitent.findAll", query = "SELECT k FROM Komitent k"),
	@NamedQuery(name = "Komitent.findByIdK", query = "SELECT k FROM Komitent k WHERE k.idK = :idK"),
	@NamedQuery(name = "Komitent.findByNaziv", query = "SELECT k FROM Komitent k WHERE k.naziv = :naziv"),
	@NamedQuery(name = "Komitent.findByAdresa", query = "SELECT k FROM Komitent k WHERE k.adresa = :adresa"),
	@NamedQuery(name = "Komitent.findBySediste", query = "SELECT k FROM Komitent k WHERE k.sediste = :sediste")})
public class Komitent implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdK")
	private Integer idK;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Naziv")
	private String naziv;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Adresa")
	private String adresa;
	@Column(name = "Sediste")
	private Integer sediste;

	public Komitent() {
	}

	public Komitent(Integer idK) {
		this.idK = idK;
	}

	public Komitent(Integer idK, String naziv, String adresa) {
		this.idK = idK;
		this.naziv = naziv;
		this.adresa = adresa;
	}

	public Integer getIdK() {
		return idK;
	}

	public void setIdK(Integer idK) {
		this.idK = idK;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public String getAdresa() {
		return adresa;
	}

	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	public Integer getSediste() {
		return sediste;
	}

	public void setSediste(Integer sediste) {
		this.sediste = sediste;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idK != null ? idK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Komitent)) {
			return false;
		}
		Komitent other = (Komitent) object;
		if ((this.idK == null && other.idK != null) || (this.idK != null && !this.idK.equals(other.idK)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "entiteti.Komitent[ idK=" + idK + " ]";
	}

}
