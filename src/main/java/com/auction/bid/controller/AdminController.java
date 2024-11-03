package com.auction.bid.controller;

import com.auction.bid.model.Item;
import com.auction.bid.model.Bid;
import com.auction.bid.service.BidService;
import com.auction.bid.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final BidService bidService;

    public AdminController(ItemService itemService, BidService bidService) {
        this.itemService = itemService;
        this.bidService = bidService;
    }

    private final String adminUsername = "auction_admin";
    private final String adminPassword = "password123";

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute("admin", true);
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        return "admin/dashboard";
    }

    @GetMapping("/items")
    public String viewItems(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "admin/items";
    }

    @GetMapping("/items/new")
    public String showCreateItemForm(Model model) {
        model.addAttribute("item", new Item());
        return "admin/create-item";
    }

    @PostMapping("/items")
    public String createItem(@ModelAttribute Item item, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Photo is required for the item.");
            return "redirect:/admin/items/new";
        }

        itemService.saveItem(item, file);
        redirectAttributes.addFlashAttribute("success", "Item created successfully!");
        return "redirect:/admin/items";
    }

    @GetMapping("/items/edit/{id}")
    public String showEditItemForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Item> itemOptional = itemService.getItemById(id);
        if (itemOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Item not found.");
            return "redirect:/admin/items";
        }
        model.addAttribute("item", itemOptional.get());
        return "admin/edit-item";
    }

    @PostMapping("/items/update/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute Item item, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        itemService.updateItem(id, item, file);
        redirectAttributes.addFlashAttribute("success", "Item updated successfully!");
        return "redirect:/admin/items";
    }

    @GetMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        itemService.deleteItem(id);
        redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
        return "redirect:/admin/items";
    }

    @GetMapping("/bids")
    public String viewBids(@RequestParam(required = false) String status, Model model) {
        List<Bid> bids;
        if (status != null && !status.isEmpty()) {
            bids = bidService.getBidsByStatus(status);
        } else {
            bids = bidService.getAllBidsSortedByStatus();
        }
        model.addAttribute("bids", bids);
        model.addAttribute("status", status);
        return "admin/bids";
    }

    @PostMapping("/bids/approve/{id}")
    public String approveBid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bidService.approveBid(id);
        redirectAttributes.addFlashAttribute("success", "Bid approved successfully!");
        return "redirect:/admin/bids";
    }

    @PostMapping("/bids/deny/{id}")
    public String denyBid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bidService.denyBid(id);
        redirectAttributes.addFlashAttribute("success", "Bid denied successfully!");
        return "redirect:/admin/bids";
    }
}
