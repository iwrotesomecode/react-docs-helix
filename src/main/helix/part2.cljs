(ns helix.part2
  (:require [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

;;;; Thinking in React

(def products2
  [{:category "Fruits", :price "$1", :stocked true, :name "Apple"},
   {:category "Fruits", :price "$1", :stocked true, :name "Dragonfruit"},
   {:category "Fruits", :price "$2", :stocked false, :name "Passionfruit"},
   {:category "Vegetables", :price "$2", :stocked true, :name "Spinach"},
   {:category "Vegetables", :price "$4", :stocked false, :name "Pumpkin"},
   {:category "Vegetables", :price "$1", :stocked true, :name "Peas"}])

;; FilterableProductTable
;; - SearchBar
;; - ProductTable
;; - - ProductCategoryRow
;; - - ProductRow

(defnc product-category-row
  [{:keys [category]}]
  (d/tr
   (d/th {:colSpan 2} category)))

(defnc product-row
  [{{:keys [price stocked name]} :data}]
  (let [name (if stocked name (d/span {:style {:color "red"}} name))]
    (d/tr
     (d/td name)
     (d/td price))))

(defnc search-bar
  [{:keys [filter-text in-stock-only on-filter-text-change on-in-stock-only-change]}]
  (d/form
   (d/input {:type "text"
             :value filter-text
             :placeholder "Search..."
             :on-change #(on-filter-text-change (.. % -target -value))})
   (d/br)
   (d/input {:type "checkbox"
             :checked in-stock-only
             :name "filter"
             :on-change #(on-in-stock-only-change (.. % -target -checked))})
   (d/label {:for "filter"} "Only show products in stock")))

(defnc product-table
  [{:keys [products filter-text in-stock-only]}]
  (let [products (if in-stock-only (filter #(:stocked %) products) products)
        products (filter #(not= -1 (.indexOf (.toLowerCase (:name %)) (.toLowerCase filter-text))) products)
        groups (group-by :category products)]
    (d/table
     (d/thead
      (d/tr
       (d/th "Name")
       (d/th "Price")))
     (d/tbody
      (mapcat
       (fn [[group data]]
         (cons
          ($ product-category-row {:category group :key group})
          (for [row data]
            ($ product-row {:data row :key (:name row)}))))
       groups)))))
 ;; this originally below tbody, lilactown's version above.
 ;; (for [[group data] groups]
 ;;        (<> {:key group}  ;; key should go on <>
 ;;            ($ product-category-row {:category group})
 ;;            (for [row data]
 ;;              ($ product-row {:data row :key (:name row)}))))))))

(defnc filterable-product-table
  [{:keys [products]}]
  (let [[filter-text set-filter-text] (hooks/use-state "")
        [in-stock-only set-in-stock-only] (hooks/use-state false)]
    (d/div
     ($ search-bar {:filter-text filter-text
                    :in-stock-only in-stock-only
                    :on-filter-text-change set-filter-text
                    :on-in-stock-only-change set-in-stock-only})
     ($ product-table {:products products
                       :filter-text filter-text
                       :in-stock-only in-stock-only
                       :on-filter-text-change set-filter-text
                       :on-in-stock-only-change set-in-stock-only}))))
