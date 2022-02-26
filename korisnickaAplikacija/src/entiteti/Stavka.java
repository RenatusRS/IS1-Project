package entiteti;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "stavka")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Stavka.findAll", query = "SELECT s FROM Stavka s"),
	@NamedQuery(name = "Stavka.findByIdSta", query = "SELECT s FROM Stavka s WHERE s.idSta = :idSta"),
	@NamedQuery(name = "Stavka.findByRedBroj", query = "SELECT s FROM Stavka s WHERE s.redBroj = :redBroj"),
	@NamedQuery(name = "Stavka.findByDatum", query = "SELECT s FROM Stavka s WHERE s.datum = :datum"),
	@NamedQuery(name = "Stavka.findByIznos", query = "SELECT s FROM Stavka s WHERE s.iznos = :iznos"),
	@NamedQuery(name = "Stavka.findByTip", query = "SELECT s FROM Stavka s WHERE s.tip = :tip"),
	@NamedQuery(name = "Stavka.findByFilijala", query = "SELECT s FROM Stavka s WHERE s.filijala = :filijala")})
public class Stavka implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdSta")
	private Integer idSta;
	@Basic(optional = false)
    @NotNull
    @Column(name = "RedBroj")
	private int redBroj;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
	private Date datum;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Iznos")
	private double iznos;
	@Basic(optional = false)
    @NotNull
    @Column(name = "Tip")
	private Character tip;
	@Column(name = "Filijala")
	private Integer filijala;
	@JoinColumn(name = "Racun", referencedColumnName = "IdRac")
    @ManyToOne(optional = false)
	private Racun racun;

	public Stavka() {
	}

	public Stavka(Integer idSta) {
		this.idSta = idSta;
	}

	public Stavka(Integer idSta, int redBroj, Date datum, double iznos, Character tip) {
		this.idSta = idSta;
		this.redBroj = redBroj;
		this.datum = datum;
		this.iznos = iznos;
		this.tip = tip;
	}

	public Integer getIdSta() {
		return idSta;
	}

	public void setIdSta(Integer idSta) {
		this.idSta = idSta;
	}

	public int getRedBroj() {
		return redBroj;
	}

	public void setRedBroj(int redBroj) {
		this.redBroj = redBroj;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public double getIznos() {
		return iznos;
	}

	public void setIznos(double iznos) {
		this.iznos = iznos;
	}

	public Character getTip() {
		return tip;
	}

	public void setTip(Character tip) {
		this.tip = tip;
	}

	public Integer getFilijala() {
		return filijala;
	}

	public void setFilijala(Integer filijala) {
		this.filijala = filijala;
	}

	public Racun getRacun() {
		return racun;
	}

	public void setRacun(Racun racun) {
		this.racun = racun;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idSta != null ? idSta.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Stavka)) {
			return false;
		}
		Stavka other = (Stavka) object;
		if ((this.idSta == null && other.idSta != null) || (this.idSta != null && !this.idSta.equals(other.idSta)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "entiteti.Stavka[ idSta=" + idSta + " ]";
	}

}
