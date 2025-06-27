import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Minus, Plus, X } from "lucide-react";
import { CartItem as CartItemType } from "@/contents/products";

interface CartItemProps {
  item: CartItemType;
  onSelectItem: (id: string, selected: boolean) => void;
  onUpdateQuantity: (id: string, quantity: number) => void;
  onRemoveItem: (id: string) => void;
}

export default function CartItem({
  item,
  onSelectItem,
  onUpdateQuantity,
  onRemoveItem,
}: CartItemProps) {
  return (
    <div className="flex items-start space-x-4 p-6">
      <Checkbox
        id={`item-${item.id}`}
        checked={item.selected}
        onCheckedChange={(checked) => onSelectItem(item.id, checked as boolean)}
      />

      <div className="flex-shrink-0">
        <Image
          src={item.image}
          alt={item.name}
          width={120}
          height={120}
          className="rounded-lg object-cover"
        />
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              {item.name}
            </h3>
            <p className="text-sm text-gray-600 mb-2">
              {item.color} / {item.size}
            </p>
            <div className="flex items-center space-x-2">
              <span className="text-lg font-bold text-gray-900">
                {item.price.toLocaleString()}원
              </span>
              <span className="text-sm text-gray-500 line-through">
                {item.originalPrice.toLocaleString()}원
              </span>
            </div>
          </div>

          <Button
            variant="ghost"
            size="icon"
            onClick={() => onRemoveItem(item.id)}
            className="text-gray-400 hover:text-gray-600"
          >
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex items-center justify-between mt-4">
          <div className="flex items-center border rounded-lg">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}
              className="h-8 w-8"
              disabled={item.quantity <= 1}
            >
              <Minus className="h-3 w-3" />
            </Button>
            <span className="px-3 py-1 text-center min-w-[2.5rem]">
              {item.quantity}
            </span>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}
              className="h-8 w-8"
            >
              <Plus className="h-3 w-3" />
            </Button>
          </div>

          <div className="text-right">
            <p className="text-lg font-bold text-gray-900">
              {(item.price * item.quantity).toLocaleString()}원
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
