package main;

public class Queries {
		public static String getCustomers(long limit) {
			return "select * from tblcustomers limit " + limit;
		}
		
		public static String getRooms(long limit) {
			return "select " +
					"tblrooms.ingRoomID, " +
					"tblroomtypes.strRoomType, " +
					"tblroombands.strBandDesc, " +
					"tblroomprices.curRoomPrice, " +
					"tblrooms.strFloor, " +
					"tblrooms.memAdditionalNotes " +
					"from " +
					"tblrooms, " +
					"tblroomtypes, " +
					"tblroombands, " +
					"tblroomprices " +
					"where " +
					"tblrooms.ingRoomTypeID = tblroomtypes.ingRoomTypeID and " +
					"tblrooms.ingRoomBandID = tblroombands.ingRoomBandID and " +
					"tblrooms.ingRoomPriceID = tblroomprices.ingRoomPriceID " +
					"limit " + limit;
		}
		
		public static String getPayments(long limit) {
			return "select " +
					"tblpayments.ingPaymentID, " +
					"tblpayments.ingBookingID, " + 
					"tblpayments.ingCustomerID, " +
					"tblcustomers.txtCustomerSurnames, " +
					"tblpaymentmethods.txtPaymentMethod, " +
					"tblpayments.curPaymentAmount, " +
					"tblpayments.memPaymentComments " +
					"from " + 
					"tblpayments, " +
					"tblbookings, " +
					"tblcustomers, " +
					"tblpaymentmethods " +
					"where " +
					"tblpayments.ingBookingID = tblbookings.ingBookingID and " +
					"tblpayments.ingCustomerID = tblcustomers.ingCustomerID and " +
					"tblpayments.ingPaymentMethodID = tblpaymentmethods.ingPaymentMethodID " +
					"limit " + limit;
		}
}
