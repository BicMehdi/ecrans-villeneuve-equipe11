package com.ecrans.vente.adapter.in.sortiesflash;

import java.util.List;

public record CommandeSortiesFlash(String show_ref, List<String> seats, List<String> ticket_types,
                                  String partner_order_id, Long paid_amount_cents) {}
