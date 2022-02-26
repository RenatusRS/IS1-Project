package entiteti;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "filijala")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Filijala.findAll", query = "SELECT f FROM Filijala f"),
	@NamedQuery(name = "Filijala.findByIdFil", query = "SELECT f FROM Filijala f WHERE f.idFil = :idFil"),
	@NamedQuery(name = "Filijala.findByNaziv", query = "SELECT f FROM Filijala f WHERE f.naziv = :naziv"),
	@NamedQuery(name = "Filijala.findByAdresa", query = "SELECT f FROM Filijala f WHERE f.adresa = :adresa")})
public class Filijala implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdFil")
	private Integer idFil;
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
	@JoinColumn(name = "Mesto", referencedColumnName = "IdM")
    @ManyToOne(optional = false)
	private Mesto mesto;

	public Filijala() {
	}

	public Filijala(Integer idFil) {
		this.idFil = idFil;
	}

	public Filijala(Integer idFil, String naziv, String adresa) {
		this.idFil = idFil;
		this.naziv = naziv;
		this.adresa = adresa;
	}

	public Integer getIdFil() {
		return idFil;
	}

	public void setIdFil(Integer idFil) {
		this.idFil = idFil;
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

	public Mesto getMesto() {
		return mesto;
	}

	public void setMesto(Mesto mesto) {
		this.mesto = mesto;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idFil != null ? idFil.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Filijala)) {
			return false;
		}
		Filijala other = (Filijala) object;
		if ((this.idFil == null && other.idFil != null) || (this.idFil != null && !this.idFil.equals(other.idFil)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "entiteti.Filijala[ idFil=" + idFil + " ]";
	}

}
