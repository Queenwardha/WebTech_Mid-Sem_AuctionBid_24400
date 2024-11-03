package com.auction.bid.controller;

import com.auction.bid.model.Bidder;
import com.auction.bid.model.Item;
import com.auction.bid.service.BidService;
import com.auction.bid.service.BidderService;
import com.auction.bid.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bidder")
public class BidderController {

    private final BidderService bidderService;
    private final ItemService itemService;
    private final BidService bidService;

    public BidderController(BidderService bidderService, ItemService itemService, BidService bidService) {
        this.bidderService = bidderService;
        this.itemService = itemService;
        this.bidService = bidService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("bidder", new Bidder());
        return "bidder/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Bidder bidder) {
        bidderService.saveBidder(bidder);
        return "redirect:/bidder/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "bidder/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String phone, @RequestParam String password, HttpSession session) {
        Bidder bidder = bidderService.authenticate(phone, password);
        if (bidder != null) {
            session.setAttribute("bidder", bidder);
            return "redirect:/bidder/items";
        }
        return "bidder/login";
    }

    @GetMapping("/items")  // This is fine as is
    public String viewItems(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("items", search != null ? itemService.searchItems(search) : itemService.getAllItems());
        return "bidder/items";
    }

    @PostMapping("/bid/{itemId}")
    public String placeBid(@PathVariable Long itemId, @RequestParam Double bidAmount, HttpSession session) {
        Bidder bidder = (Bidder) session.getAttribute("bidder");
        if (bidder == null) return "redirect:/bidder/login";

        bidService.placeBid(bidder.getId(), itemId, bidAmount);
        return "redirect:/bidder/notifications";
    }

    @GetMapping("/notifications")
    public String viewNotifications(HttpSession session, Model model) {
        Bidder bidder = (Bidder) session.getAttribute("bidder");
        if (bidder == null) return "redirect:/bidder/login";

        model.addAttribute("notifications", bidService.getNotificationsForBidder(bidder.getId()));
        return "bidder/notifications";
    }

    @GetMapping("/download/{itemId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long itemId) {
        return itemService.getItemById(itemId)
                .map(item -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(item.getPhoto()))
                .orElse(ResponseEntity.notFound().build());
    }
}
