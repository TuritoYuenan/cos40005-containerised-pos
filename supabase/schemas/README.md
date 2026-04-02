# Containerised POS Database Schema

## File format

Ensure:

1. Categorised by entity type in order (enum, table, function)
2. Ordered by dependency

Format:

```
<entity type number>.<order number>_<name_in_snake_case>.sql
```
