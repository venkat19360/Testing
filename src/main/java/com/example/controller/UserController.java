package com.example.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.Dao.UserRepository;
import com.example.entity.User;
import com.example.service.UserService;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
//import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.properties.TextAlignment;

@Controller
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	private UserRepository userRepo;

	@GetMapping("/testForApi")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Employee Registration is Live...");
	}

	@GetMapping("/testForDb")
	public ResponseEntity<String> testForDb() {
		List<User> all = userRepo.findAll();
		if (all != null) {
			return ResponseEntity.ok("DataBase is Working");
		} else {
			return ResponseEntity.ok("DataBase is Offline");
		}
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());

		// List of Indian states
		List<String> states = List.of("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
				"Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh",
				"Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim",
				"Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal");

		model.addAttribute("states", states);
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user, Model model) {
		userService.saveUser(user);
		model.addAttribute("userId", user.getId());
		return "success";
	}

	@GetMapping("/download/{id}/document")
	public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable Long id) {
		User user = userService.getUser(id);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		try (XWPFDocument document = new XWPFDocument()) {
			document.createParagraph().createRun().setText("Employee Information:");
			document.createParagraph().createRun().setText("Full Name: " + user.getFullName());
			document.createParagraph().createRun().setText("Email: " + user.getEmail());
			document.createParagraph().createRun().setText("Phone Number: " + user.getPhoneNumber());
			document.createParagraph().createRun().setText("State: " + user.getState());
			document.createParagraph().createRun().setText("City: " + user.getCity());
			document.createParagraph().createRun().setText("Address: " + user.getAddress());
			document.createParagraph().createRun().setText("Gender: " + user.getGender());
			document.createParagraph().createRun().setText("Role: " + user.getRole());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=user.docx");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(in));
		} catch (IOException e) {
			return ResponseEntity.status(500).build();
		}
	}

	@GetMapping("/download/{id}/pdf")
	public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Long id) {
		User user = userService.getUser(id);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			Text title = new Text("Employee Information:").setBold();
			Paragraph paragraph = new Paragraph(title).setTextAlignment(TextAlignment.CENTER);

			document.add(paragraph);
			document.add(new Paragraph("Full Name: " + user.getFullName()));
			document.add(new Paragraph("Email: " + user.getEmail()));
			document.add(new Paragraph("Phone Number: " + user.getPhoneNumber()));
			document.add(new Paragraph("State: " + user.getState()));
			document.add(new Paragraph("City: " + user.getCity()));
			document.add(new Paragraph("Address: " + user.getAddress()));
			document.add(new Paragraph("Gender: " + user.getGender()));
			document.add(new Paragraph("Role: " + user.getRole()));

			document.close();
		} catch (Exception e) {
			return ResponseEntity.status(500).build();
		}

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=user.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(in));
	}
}
