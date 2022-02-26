package entiteti;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "mesto")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Mesto.findAll", query = "SELECT m FROM Mesto m"),
	@NamedQuery(name = "Mesto.findByIdMes", query = "SELECT m FROM Mesto m WHERE m.idMes = :idMes"),
	@NamedQuery(name = "Mesto.findByPostBr", query = "SELECT m FROM Mesto m WHERE m.postBr = :postBr"),
	@NamedQuery(name = "Mesto.findByNaziv", query = "SELECT m FROM Mesto m WHERE m.naziv = :naziv")})
public class Mesto implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdMes")
	private Integer idMes;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "PostBr")
	private String postBr;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Naziv")
	private String naziv;

	public Mesto() {
	}

	public Mesto(Integer idMes) {
		this.idMes = idMes;
	}

	public Mesto(Integer idMes, String postBr, String naziv) {
		this.idMes = idMes;
		this.postBr = postBr;
		this.naziv = naziv;
	}

	public Integer getIdMes() {
		return idMes;
	}

	public void setIdMes(Integer idMes) {
		this.idMes = idMes;
	}

	public String getPostBr() {
		return postBr;
	}

	public void setPostBr(String postBr) {
		this.postBr = postBr;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idMes != null ? idMes.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Mesto)) {
			return false;
		}
		Mesto other = (Mesto) object;
		if ((this.idMes == null && other.idMes != null) || (this.idMes != null && !this.idMes.equals(other.idMes)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "entiteti.Mesto[ idMes=" + idMes + " ]";
	}

}
