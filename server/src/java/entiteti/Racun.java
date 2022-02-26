package entiteti;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "racun")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Racun.findAll", query = "SELECT r FROM Racun r"),
	@NamedQuery(name = "Racun.findByIdRac", query = "SELECT r FROM Racun r WHERE r.idRac = :idRac"),
	@NamedQuery(name = "Racun.findByStatus", query = "SELECT r FROM Racun r WHERE r.status = :status"),
	@NamedQuery(name = "Racun.findByBrojStavki", query = "SELECT r FROM Racun r WHERE r.brojStavki = :brojStavki"),
	@NamedQuery(name = "Racun.findByDozvMinus", query = "SELECT r FROM Racun r WHERE r.dozvMinus = :dozvMinus"),
	@NamedQuery(name = "Racun.findByStanje", query = "SELECT r FROM Racun r WHERE r.stanje = :stanje"),
	@NamedQuery(name = "Racun.findByDatum", query = "SELECT r FROM Racun r WHERE r.datum = :datum"),
	@NamedQuery(name = "Racun.findByFilijala", query = "SELECT r FROM Racun r WHERE r.filijala = :filijala"),
	@NamedQuery(name = "Racun.findByKomitent", query = "SELECT r FROM Racun r WHERE r.komitent = :komitent")})
public class Racun implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdRac")
	private Integer idRac;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Status")
	private Character status;
	@Basic(optional = false)
    @NotNull
    @Column(name = "BrojStavki")
	private int brojStavki;
	@Basic(optional = false)
    @NotNull
    @Column(name = "DozvMinus")
	private double dozvMinus;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Stanje")
	private double stanje;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
	private Date datum;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Filijala")
	private int filijala;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Komitent")
	private int komitent;

	public Racun() {
	}

	public Racun(Integer idRac) {
		this.idRac = idRac;
	}

	public Racun(Integer idRac, Character status, int brojStavki, double dozvMinus, double stanje, Date datum, int filijala, int komitent) {
		this.idRac = idRac;
		this.status = status;
		this.brojStavki = brojStavki;
		this.dozvMinus = dozvMinus;
		this.stanje = stanje;
		this.datum = datum;
		this.filijala = filijala;
		this.komitent = komitent;
	}

	public Integer getIdRac() {
		return idRac;
	}

	public void setIdRac(Integer idRac) {
		this.idRac = idRac;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public int getBrojStavki() {
		return brojStavki;
	}

	public void setBrojStavki(int brojStavki) {
		this.brojStavki = brojStavki;
	}

	public double getDozvMinus() {
		return dozvMinus;
	}

	public void setDozvMinus(double dozvMinus) {
		this.dozvMinus = dozvMinus;
	}

	public double getStanje() {
		return stanje;
	}

	public void setStanje(double stanje) {
		this.stanje = stanje;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public int getFilijala() {
		return filijala;
	}

	public void setFilijala(int filijala) {
		this.filijala = filijala;
	}

	public int getKomitent() {
		return komitent;
	}

	public void setKomitent(int komitent) {
		this.komitent = komitent;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idRac != null ? idRac.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Racun)) {
			return false;
		}
		Racun other = (Racun) object;
		if ((this.idRac == null && other.idRac != null) || (this.idRac != null && !this.idRac.equals(other.idRac)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "entiteti.Racun[ idRac=" + idRac + " ]";
	}

}
