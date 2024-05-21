package com.leka.teashop.controller;

import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.ProductType;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.MediaService;
import com.leka.teashop.service.ProductService;
import com.leka.teashop.utils.PageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {

    @Value("${web.pageable.default-page-size}")
    private int defaultPageSize;
    private Map<String, Boolean> filters = new HashMap<>();
    private final ProductService productService;
    private final MediaService mediaService;
    private final ProductMapper productMapper;

    @GetMapping("/product")
    public String addProduct(@ModelAttribute("request") ProductDto dto,
                             @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                             Model model) {
        model.addAttribute("page", page);
        return "add-product";
    }

    @GetMapping("/allProducts")
    public String getAllProducts(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "size", required = false) Integer pageSize,
                                 @RequestParam(name = "sort", defaultValue = "name") String sortField,
                                 @RequestParam(name = "dir", defaultValue = "asc") String sortDirection,
                                 Model model) {
        if (pageSize == null) {
            pageSize = defaultPageSize;
        }
        PageContext pageContext = new PageContext(pageNo, pageSize, sortField, sortDirection);
        Page<ProductDto> dtoList = productService.getAllProductsForAdmin(pageContext);
        addAttributesToModel(model, dtoList, pageContext);
        return "list-of-products";
    }

    @GetMapping("/editProduct/{id}")
    public String showUpdateForm(@PathVariable("id") Long productId,
                                 @RequestParam(name = "page", required = false) Integer page,
                                 Model model) {
        Product productFromDB = productService.findById(productId);
        model.addAttribute("product", productMapper.toDto(productFromDB));
        model.addAttribute("page", page);
        return "update-product";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("files") List<MultipartFile> files,
                             @Valid @ModelAttribute("request") ProductDto request, BindingResult result,
                             @RequestParam(name = "page", required = false) Integer page, Model model) {
        if (result.hasErrors()) {
            return addProduct(request, page, model);
        }
        productService.addProduct(request, files);
        return "redirect:/allProducts?page=" + page + "&addSuccess";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteById(id);
        return "redirect:/allProducts";
    }

    @GetMapping("/activate/{id}")
    public String activateProduct(@PathVariable(name = "id") Long id) {
        productService.activateById(id);
        return "redirect:/allProducts";
    }

    @PostMapping("/updateProduct/{id}")
    public String updateProduct(@Valid @ModelAttribute("request") ProductDto request,
                                //BindingResult should be immediate to @Valid object.
                                BindingResult result, Model model,
                                @PathVariable(name = "id") Long id,
                                @RequestParam(name = "files", required = false) List<MultipartFile> files,
                                @RequestParam(name = "page", required = false) Integer page,
                                HttpServletRequest servletRequest) {
        if (result.hasErrors()) {
            return showUpdateForm(id, page, model);
        }
        String deleteImages = servletRequest.getParameter("deleteImage");
        if (!files.get(0).isEmpty() && "on".equals(deleteImages)) {
            return "redirect:/editProduct/" + id + "?page=" + page + "&oneOptionMustBeChosen";
        }
        productService.updateProduct(request, files, deleteImages);
        return "redirect:/allProducts?page=" + page + "&updateSuccess";
    }

    @ResponseBody
    @GetMapping("/image/{id}")
    public byte[] getImageById(@PathVariable("id") Long id) {
        return mediaService.getImageByIdWithData(id).getData();
    }

    @PostMapping("/applyFilters")
    public String applyFilters(@RequestParam(name = "tea", required = false) String teaFilter,
                               @RequestParam(name = "mushroom", required = false) String mushroomFilter,
                               @RequestParam(name = "jam", required = false) String jamFilter,
                               @RequestParam(name = "herbs", required = false) String herbFilter,
                               @RequestParam(name = "others", required = false) String othersFilter,
                               @RequestParam(name = "showAll", required = false) String showAll) {
        filters.clear();
        if ("on".equals(showAll)) {
            return "redirect:/showAllProductsForSale?page=1&size=" + defaultPageSize;
        }
        filters.put(ProductType.TEA.name(), "on".equalsIgnoreCase(teaFilter));
        filters.put(ProductType.MUSHROOMS.name(), "on".equalsIgnoreCase(mushroomFilter));
        filters.put(ProductType.JAMS.name(), "on".equalsIgnoreCase(jamFilter));
        filters.put(ProductType.HERBS.name(), "on".equalsIgnoreCase(herbFilter));
        filters.put(ProductType.OTHERS.name(), "on".equalsIgnoreCase(othersFilter));
        return "redirect:/showAllProductsForSale?page=1&size=" + defaultPageSize;
    }

    @GetMapping("/showAllProductsForSale")
    public String getAllProductsForSale(@RequestParam(name = "page", defaultValue = "1") Integer pageNo,
                                        @RequestParam(name = "size", required = false) Integer pageSize,
                                        @RequestParam(name = "sort", defaultValue = "name") String sortField,
                                        @RequestParam(name = "dir", defaultValue = "asc") String sortDirection,
                                        @RequestParam(value = "search", required = false) String search,
                                        Model model) {
        if (pageSize == null) {
            pageSize = defaultPageSize;
        }
        Page<ProductDto> dtoList;
        PageContext pageContext;
        if (search == null || "null".equals(search)) {
            pageContext = new PageContext(pageNo, pageSize, sortField, sortDirection);
            if (filters.isEmpty()) {
                dtoList = productService.getAllProductsForSale(pageContext);
            } else {
                dtoList = productService.getAllProductsForSale(pageContext, filters);
            }
        } else {
            pageContext = new PageContext(pageNo, pageSize);
            dtoList = productService.getAllProductsBySearch(search, pageContext);
            if (dtoList.isEmpty()) {
                filters.clear();
                return "redirect:/showAllProductsForSale?noSearchResults";
            }
        }
        addAttributesToModel(model, dtoList, pageContext);
        model.addAttribute("search", search);
        return "products-for-sale";
    }

    @RequestMapping("/search")
    public String search(@RequestParam(value = "search", required = false) String search) {
        if (search == null || search.isEmpty()) {
            return "redirect:/showAllProductsForSale";
        }
        return "redirect:/showAllProductsForSale?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
    }

    private void addAttributesToModel(Model model, Page<ProductDto> dtoList, PageContext pageContext) {
        model.addAttribute("products", dtoList.getContent());
        model.addAttribute("currentPage", pageContext.getPageNo());
        model.addAttribute("totalPages", dtoList.getTotalPages());
        model.addAttribute("totalItems", dtoList.getTotalElements());
        model.addAttribute("sortField", pageContext.getSortField() == null ? "name" : pageContext.getSortField());
        model.addAttribute("sortDir", pageContext.getSortDirection() == null ? "asc" : pageContext.getSortDirection());
        model.addAttribute("reverseSortDir",
                Sort.Direction.ASC.name().equalsIgnoreCase(pageContext.getSortDirection()) ? "desc" : "asc");
    }
}
