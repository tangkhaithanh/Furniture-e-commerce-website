package vn.iotstar.controllers.vendor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.iotstar.dto.RevenueDTO;
import vn.iotstar.entity.Order;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.DashBoardService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.RevenueService;

@Controller
@RequestMapping("/vendor/revenue")
public class VendorRevenueController 
{
	@Autowired
	private DashBoardService dashBoardService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private RevenueService revenueService;
	@GetMapping()
	public String revenuePage()
	{
		return "manageRevenue";
	}
	@GetMapping("/overview")
	public String showDashboard(Model model) 
	{
        // Lấy các dữ liệu từ service
        long totalOrders = dashBoardService.getTotalOrders();
        BigDecimal totalRevenue = dashBoardService.getTotalRevenue();
		/* long totalCustomers = dashBoardService.getTotalCustomers(); */
        double customerSatisfaction = dashBoardService.getCustomerSatisfaction();

        // Đưa dữ liệu vào model để gửi ra view (index.html)
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
		/* model.addAttribute("totalCustomers", totalCustomers); */
        model.addAttribute("customerSatisfaction", customerSatisfaction);

        return "OverViewRevenue";  // Chuyển đến view index.html
    }
	   @GetMapping("/receipt")
	    public String listOrders(
	            @RequestParam(name = "search", required = false, defaultValue = "") String search,
	            @RequestParam(name = "status", required = false, defaultValue = "") String status,
	            @RequestParam(name = "page", defaultValue = "0") int page,
	            Model model) 
	   {
	        
	        // Số đơn hàng trên mỗi trang
	        int size = 5;
	        
	        // Gọi service để lấy dữ liệu
	        Page<Order> orders = orderService.getOrders(search, status, page, size);
	        
	        // Thêm dữ liệu vào model
	        model.addAttribute("orders", orders);
	        model.addAttribute("search", search);
	        model.addAttribute("status", status);
	        
	        // Tính toán số trang
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", orders.getTotalPages());
	        
	        return "manageReceipt";
	    }
	   @GetMapping("/chart")
	    public String getRevenueChart() {
	        return "revenueChart"; // This will return revenueChart.html view
	    }
	// Add new endpoint for API
	    @GetMapping("/api/chart-data")
	    @ResponseBody
	    public ResponseEntity<?> getRevenueData(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	    ) {
	        try {
	            if (startDate == null) {
	                startDate = LocalDate.now().minusDays(30);
	            }
	            if (endDate == null) {
	                endDate = LocalDate.now();
	            }
	            
	            RevenueDTO revenueData = revenueService.getRevenueData(startDate, endDate);
	            return ResponseEntity.ok(revenueData);
	        } catch (Exception e) {
	            return ResponseEntity.badRequest()
	                .body("Error: " + e.getMessage());
	        }
	    }
}
	