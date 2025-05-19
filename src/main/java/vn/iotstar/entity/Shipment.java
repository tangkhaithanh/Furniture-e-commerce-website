package vn.iotstar.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Shipments")
public class Shipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "shipment_id")
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "order_id", nullable = false)
	    private Order order;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "shipper_id", nullable = false)
	    private User shipper;

	    @Column(name = "status", nullable = false)
	    private String status;

	    @Column(name = "shipped_at", nullable = false, updatable = false)
	    private LocalDateTime shippedAt;
}
