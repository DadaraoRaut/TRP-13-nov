FROM nginx:alpine

COPY dist/erp-supermarket/browser /usr/share/nginx/html

RUN chmod -R 755 /usr/share/nginx/html

COPY default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
