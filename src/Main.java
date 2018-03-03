import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class Main {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		Configuration configuration = new Configuration().configure()
				.addAnnotatedClass(SalesTransactions.class);
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();
		SessionFactory sessionFactory = configuration
				.buildSessionFactory(serviceRegistry);

		// store list of Sale transaction to add to sales table
		// java date constructor: Date(year + 1900, month + 1, day)
		// i.e Date(110, 4, 5) => 2010-05-05
		ArrayList<SalesTransactions> list = new ArrayList<SalesTransactions>();
		list.add(new SalesTransactions(new Date(110, 1, 1), "Books", 3, 4.99));
		list.add(new SalesTransactions(new Date(101, 3, 3), "Phone", 5, 7.99));
		list.add(new SalesTransactions(new Date(102, 1, 11), "Car", 1, 25000.99));
		list.add(new SalesTransactions(new Date(117, 1, 5), "Shoes", 10, 99.99));
		list.add(new SalesTransactions(new Date(117, 7, 7), "Shirt", 3, 9.99));
		list.add(new SalesTransactions(new Date(117, 3, 20), "Pants", 7, 39.99));
		list.add(new SalesTransactions(new Date(117, 2, 20), "Pants", 7, 39.99));

		addSalesTransactions(sessionFactory, list);

		// find sales transaction that happened on Date(110, 1, 1)
		Date date = new Date(110, 1, 1);
		SingleTransaction(sessionFactory, date);

		// Retrieve sales transactions for a given product over a given time
		// interval
		// in this case (100, 1, 1) and (105, 11, 20)
		Date date1 = new Date(100, 1, 1);
		Date date2 = new Date(105, 11, 20);
		ProductOverTimeInterval(sessionFactory, date1, date2);

		// aggregate operation on sales transaction objects
		AggregateOperation(sessionFactory, "Pants");
	}

	/**
	 * aggregate operation on sales transaction objects How much sales of a
	 * given product item
	 * 
	 * @param sessionFactory: creating hibernate sessions to query
	 * @param productName: name of product
	 */
	public static void AggregateOperation(SessionFactory sessionFactory,
			String productName) {
		Session session = sessionFactory.openSession();
		String hql = "SELECT count(sales.ProductName) FROM Sales sales WHERE sales.ProductName = :ProductName";
		Query query = session.createQuery(hql);
		query.setParameter("ProductName", productName);
		List results = query.list();
		System.out.println("Aggregation Operation, How much sales of "
				+ productName + ":");
		Number number = (Number) results.get(0);
		System.out.println(number.intValue());

		session.close();

	}

	/**
	 * find sales transactions between two time intervals
	 * 
	 * @param sessionFactory: creating hibernate sessions to query
	 * @param date1: sales transactions after date1
	 * @param date2: sales transactions before date2
	 */
	public static void ProductOverTimeInterval(SessionFactory sessionFactory,
			Date date1, Date date2) {
		Session session = sessionFactory.openSession();
		String hql = "FROM Sales WHERE Date BETWEEN :date1 AND :date2";
		Query query = session.createQuery(hql);
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		List<SalesTransactions> result = query.list();

		System.out.println("Product Over Time Interval "
				+ new SimpleDateFormat("yyyy-MM-dd").format(date1) + " and "
				+ new SimpleDateFormat("yyyy-MM-dd").format(date2) + ":");
		print(session, query.list());
	}

	/**
	 * print a single transaction based on the date
	 * 
	 * @param sessionFactory: creating hibernate sessions to query
	 * @param date: date in which transaction is in
	 */
	public static void SingleTransaction(SessionFactory sessionFactory,
			Date date) {
		Session session = sessionFactory.openSession();
		String hql = "FROM Sales WHERE Date = :Date";
		Query query = session.createQuery(hql);
		query.setParameter("Date", date);
		System.out.println("Single Transaction on "
				+ new SimpleDateFormat("yyyy-MM-dd").format(date) + ":");
		print(session, query.list());

	}

	/**
	 * prints the result list on the queries
	 * 
	 * @param session: given session to close
	 * @param result: result list on the query
	 */
	public static void print(Session session, List<SalesTransactions> result) {
		SalesTransactions salesTransaction = null;
		for (SalesTransactions s : result) {
			System.out.println("SalesTransaction: " + "Date = " + s.getDate()
					+ " ProductName: " + s.getProductName() + " Quantity: "
					+ s.getQuantity() + " UnitCost: " + s.getUnitCost()
					+ " TotalCost: " + s.getTotalCost());
		}
		session.close();
	}

	/**
	 * adds sales transactions to sales table
	 * 
	 * @param sessionFactory: creating hibernate sessions
	 * @param list: list of sales transactions to add to sales table
	 */
	public static void addSalesTransactions(SessionFactory sessionFactory,
			ArrayList<SalesTransactions> list) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for (SalesTransactions s : list) {
				session.save(s);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

}
