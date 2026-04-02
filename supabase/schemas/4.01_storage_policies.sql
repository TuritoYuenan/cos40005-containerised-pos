create policy "Enable insert for users "
  on "storage"."buckets"
  as permissive
  for insert
  to public
with check (true);


create policy "Enable read access for all users"
  on "storage"."buckets"
  as permissive
  for select
  to public
using (true);


create policy "Give anon users access to JPG images in folder 1ffg0oo_0"
  on "storage"."objects"
  as permissive
  for select
  to public
using (((bucket_id = 'images'::text) AND (auth.role() = 'anon'::text)));


create policy "Give anon users access to JPG images in folder 1ffg0oo_1"
  on "storage"."objects"
  as permissive
  for insert
  to public
with check (((bucket_id = 'images'::text) AND (auth.role() = 'anon'::text)));


create policy "Give anon users access to JPG images in folder 1ffg0oo_2"
  on "storage"."objects"
  as permissive
  for update
  to public
using (((bucket_id = 'images'::text) AND (auth.role() = 'anon'::text)));
