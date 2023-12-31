package com.leka.teashop.controller;

import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.model.Product;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.MediaService;
import com.leka.teashop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    @Value("${web.pageable.default-page-size}")
    private int defaultPageSize;
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

    @GetMapping({"/allProducts", "/showAllProductsForSale"})
    public String getAllProducts(@RequestParam(name = "page", required = false) Integer pageNo,
                                 @RequestParam(name = "size", required = false) Integer pageSize,
                                 @RequestParam(name = "sort", required = false) String sortField,
                                 @RequestParam(name = "dir", required = false) String sortDirection,
                                 Model model, HttpServletRequest httpServletRequest) {
        String urlPath = httpServletRequest.getRequestURI();
        urlPath = urlPath.substring(urlPath.lastIndexOf('/') + 1);
        if (sortDirection == null) {
            sortDirection = "asc";
        }
        if (sortField == null) {
            sortField = "name";
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = defaultPageSize;
        }
        Page<ProductDto> dtoList = productService.getAllProducts(pageNo, pageSize, sortField, sortDirection, urlPath);
        List<ProductDto> productDtoList = dtoList.getContent();

        model.addAttribute("products", productDtoList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", dtoList.getTotalPages());
        model.addAttribute("totalItems", dtoList.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDirection);
        model.addAttribute("reverseSortDir",
                Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? "desc" : "asc");
        return urlPath.equals("allProducts") ? "list-of-products" : "products-for-sale";
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
}
