import java.util.Date;
import javax.persistence.*;

@Entity(name = "Sales")
public class SalesTransactions {

	@Id
	@Column(name = "Date")
	@Temporal(TemporalType.DATE)
	private Date Date;

	@Column(name = "ProductName")
	private String ProductName;

	@Column(name = "Quantity")
	private int Quantity;

	@Column(name = "UnitCost")
	private double UnitCost;

	@Column(name = "TotalCost")
	private double TotalCost;

	public Date getDate() {
		return Date;
	}

	public void setDate(Date Date) {
		this.Date = Date;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	public double getUnitCost() {
		return UnitCost;
	}

	public void setUnitCost(double unitCost) {
		UnitCost = unitCost;
	}

	public double getTotalCost() {
		return TotalCost;
	}

	public void setTotalCost(double totalCost) {
		TotalCost = totalCost;
	}

	public SalesTransactions() {
	}

	public SalesTransactions(Date Date, String ProductName, int Quantity,
			double UnitCost) {
		this.Date = Date;
		this.ProductName = ProductName;
		this.Quantity = Quantity;
		this.UnitCost = UnitCost;
		this.TotalCost = Quantity * UnitCost;
	}
}
