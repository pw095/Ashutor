INSERT
  INTO tmp_balance_item
  (
    emitter_name,
    file_name,
    item_index,
    parent_item_index,
    item_level,
    item_header_flag,
    item_subtotal_flag,
    item_name,
    item_pure_name,
    tech_update_date
  )
VALUES
  (
    :emitter_name,
    :file_name,
    :item_index,
    :parent_item_index,
    :item_level,
    :item_header_flag,
    :item_subtotal_flag,
    :item_name,
    :item_pure_name,
    :tech_update_date
  )
